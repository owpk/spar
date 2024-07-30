package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.CurrencyEntity;
import ru.sparural.engine.entity.Item;
import ru.sparural.engine.entity.LoymaxChecksItem;
import ru.sparural.engine.repositories.ItemRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ChecksItems;
import ru.sparural.tables.LoymaxChecksItems;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;

@Service
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final DSLContext dslContext;
    private final ChecksItems table = ChecksItems.CHECKS_ITEMS;
    private final LoymaxChecksItems loymaxTable = LoymaxChecksItems.LOYMAX_CHECKS_ITEMS;


    @Override
    public Optional<Item> save(Item entity) {
        return dslContext
                .insertInto(table)
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CHECK_ID, entity.getCheckId())
                .set(table.COUNT, entity.getCount())
                .set(table.POSITION_ID, entity.getPositionId())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.UNIT, entity.getUnit())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict()
                .doNothing()
                .returning()
                .fetchOptionalInto(Item.class);
    }

    @Override
    public Optional<Item> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(Item.class);
    }

    @Override
    public Optional<Item> update(Item entity) {
        return dslContext
                .update(table)
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CHECK_ID, entity.getCheckId())
                .set(table.COUNT, entity.getCount())
                .set(table.POSITION_ID, entity.getPositionId())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.UNIT, entity.getUnit())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.ID.eq(entity.getId()))
                .returning()
                .fetchOptionalInto(Item.class);
    }

    @Override
    public Optional<LoymaxChecksItem> getFromLoymaxItem(String itemId) {
        return dslContext
                .selectFrom(loymaxTable)
                .where(loymaxTable.ITEM_ID.eq(itemId))
                .fetchOptionalInto(LoymaxChecksItem.class);
    }

    @Override
    public void saveLoymaxItem(Long checkItemId, String itemId) {
        dslContext
                .insertInto(loymaxTable)
                .set(loymaxTable.CHECK_ITEM_ID, checkItemId)
                .set(loymaxTable.ITEM_ID, itemId)
                .set(loymaxTable.CREATED_AT, TimeHelper.currentTime())
                .set(loymaxTable.UPDATED_AT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<Item> getListByCheckId(Long checkId) {
        return dslContext
                .selectFrom(table)
                .where(table.CHECK_ID.eq(checkId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(Item.class);
    }

    @Override
    public List<Item> batchSave(List<Item> itemList) {
        var batchQueries = itemList.stream()
                .map(entity -> dslContext.insertInto(table)
                        .set(table.AMOUNT, entity.getAmount())
                        .set(table.CHECK_ID, entity.getCheckId())
                        .set(table.COUNT, entity.getCount())
                        .set(table.POSITION_ID, entity.getPositionId())
                        .set(table.DESCRIPTION, entity.getDescription())
                        .set(table.UNIT, entity.getUnit())
                        .set(table.CREATED_AT, TimeHelper.currentTime())
                        .onConflict(table.EXTERNAL_ID)
                        .doUpdate()
                        .set(table.AMOUNT, coalesce(table.as("excluded").AMOUNT, table.AMOUNT))
                        .set(table.CHECK_ID, coalesce(table.as("excluded").CHECK_ID, table.CHECK_ID))
                        .set(table.COUNT, coalesce(table.as("excluded").COUNT, table.COUNT))
                        .set(table.POSITION_ID, coalesce(table.as("excluded").POSITION_ID, table.POSITION_ID))
                        .set(table.DESCRIPTION, coalesce(table.as("excluded").DESCRIPTION, table.DESCRIPTION))
                        .set(table.UNIT, coalesce(table.as("excluded").UNIT, table.UNIT))
                        .set(table.UPDATED_AT, TimeHelper.currentTime())
                )
                .collect(Collectors.toList());
        dslContext.batch(batchQueries).execute();
        return dslContext.select().from(table).where(table.CHECK_ID.in(itemList.stream()
                        .map(Item::getCheckId).collect(Collectors.toList())))
                .fetchInto(Item.class);
    }

    @Override
    public List<LoymaxChecksItem> batchLoymaxSave(List<LoymaxChecksItem> list) {
        var insert = dslContext.insertInto(loymaxTable,
                loymaxTable.CHECK_ITEM_ID,
                loymaxTable.ITEM_ID,
                loymaxTable.CREATED_AT);
        for (LoymaxChecksItem entity : list) {
            insert = insert.values(entity.getCheckItemId(),
                    entity.getItemId(),
                    TimeHelper.currentTime());
        }
        return insert
                .returning()
                .fetch()
                .into(LoymaxChecksItem.class);
    }

}
