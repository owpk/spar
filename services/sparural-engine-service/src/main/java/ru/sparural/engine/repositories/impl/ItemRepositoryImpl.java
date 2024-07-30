package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Item;
import ru.sparural.engine.entity.LoymaxChecksItem;
import ru.sparural.engine.repositories.ItemRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.ChecksItems;
import ru.sparural.tables.LoymaxChecksItems;

import java.util.List;
import java.util.Optional;

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
                .set(table.CHECKID, entity.getCheckId())
                .set(table.COUNT, entity.getCount())
                .set(table.POSITIONID, entity.getPositionId())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.UNIT, entity.getUnit())
                .set(table.CREATEDAT, TimeHelper.currentTime())
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
                .set(table.CHECKID, entity.getCheckId())
                .set(table.COUNT, entity.getCount())
                .set(table.POSITIONID, entity.getPositionId())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.UNIT, entity.getUnit())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .where(table.ID.eq(entity.getId()))
                .returning()
                .fetchOptionalInto(Item.class);
    }

    @Override
    public Optional<LoymaxChecksItem> getFromLoymaxItem(String itemId) {
        return dslContext
                .selectFrom(loymaxTable)
                .where(loymaxTable.ITEMID.eq(itemId))
                .fetchOptionalInto(LoymaxChecksItem.class);
    }

    @Override
    public void saveLoymaxItem(Long checkItemId, String itemId) {
        dslContext
                .insertInto(loymaxTable)
                .set(loymaxTable.CHECKITEMID, checkItemId)
                .set(loymaxTable.ITEMID, itemId)
                .set(loymaxTable.CREATEDAT, TimeHelper.currentTime())
                .set(loymaxTable.UPDATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<Item> getListByCheckId(Long checkId) {
        return dslContext
                .selectFrom(table)
                .where(table.CHECKID.eq(checkId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(Item.class);
    }

    @Override
    public List<Item> batchSave(List<Item> itemList) {
        var insert = dslContext
                .insertInto(table,
                        table.AMOUNT,
                        table.CHECKID,
                        table.COUNT,
                        table.POSITIONID,
                        table.DESCRIPTION,
                        table.UNIT,
                        table.CREATEDAT);
        for (Item entity : itemList) {
            insert = insert.values(
                    entity.getAmount(),
                    entity.getCheckId(),
                    entity.getCount(),
                    entity.getPositionId(),
                    entity.getDescription(),
                    entity.getUnit(),
                    TimeHelper.currentTime());
        }
        return insert
                .onConflict()
                .doNothing()
                .returning()
                .fetch()
                .into(Item.class);
    }

    @Override
    public List<LoymaxChecksItem> batchLoymaxSave(List<LoymaxChecksItem> list) {
        var insert = dslContext.insertInto(loymaxTable,
                loymaxTable.CHECKITEMID,
                loymaxTable.ITEMID,
                loymaxTable.CREATEDAT);
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
