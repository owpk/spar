package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Merchant;
import ru.sparural.engine.entity.MerchantFormat;
import ru.sparural.engine.repositories.MerchantFormatRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.MerchantFormats;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;

@Service
@RequiredArgsConstructor
public class MerchantFormatRepositoryImpl implements MerchantFormatRepository {
    private final DSLContext dslContext;
    private final MerchantFormats table = MerchantFormats.MERCHANT_FORMATS;

    @Override
    public List<MerchantFormat> list(Integer offset, Integer limit,
                                     List<String> nameNotEqual) {
        Condition condition = DSL.trueCondition();
        if (nameNotEqual != null)
            condition = table.NAME.notIn(nameNotEqual);
        return dslContext.selectFrom(table)
                .where(condition)
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(MerchantFormat.class);
    }

    @Override
    public Optional<MerchantFormat> get(Long id) {
        return dslContext.selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(MerchantFormat.class);
    }

    @Override
    public Optional<MerchantFormat> getByName(String name) {
        return dslContext.selectFrom(table)
                .where(table.NAME.eq(name))
                .fetchOptionalInto(MerchantFormat.class);
    }

    @Override
    public Optional<MerchantFormat> create(MerchantFormat entity) {
        return dslContext.insertInto(table)
                .set(table.NAME, entity.getName())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(MerchantFormat.class);
    }

    @Override
    public List<MerchantFormat> batchSave(List<MerchantFormat> merchantFormats) {
        var batchQueries = merchantFormats.stream()
                .map(x -> dslContext.insertInto(table)
                        .set(table.NAME, x.getName())
                        .set(table.DRAFT, x.getDraft())
                        .set(table.CREATED_AT, TimeHelper.currentTime())
                        .onConflict(table.NAME)
                        .doUpdate()
                        .set(table.NAME, coalesce(table.as("excluded").NAME, table.NAME))
                        .set(table.UPDATED_AT, TimeHelper.currentTime())
                )
                .collect(Collectors.toList());
        dslContext.batch(batchQueries).execute();
        return dslContext.select().from(table).where(table.NAME.in(merchantFormats.stream()
                        .map(MerchantFormat::getName).collect(Collectors.toList())))
                .fetchInto(MerchantFormat.class);
    }


}
