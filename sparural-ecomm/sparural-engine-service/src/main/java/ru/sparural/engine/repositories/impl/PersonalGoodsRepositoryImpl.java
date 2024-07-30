package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.PersonalGoodsEntity;
import ru.sparural.engine.repositories.PersonalGoodsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.PersonalGoods;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class PersonalGoodsRepositoryImpl implements PersonalGoodsRepository {

    private final PersonalGoods table = PersonalGoods.PERSONAL_GOODS;
    private final DSLContext dslContext;

    @Override
    public Optional<PersonalGoodsEntity> getByUserIdGoodId(Long userId, Long goodsId) {
        return dslContext
                .selectFrom(table)
                .where(table.USER_ID.eq(userId))
                .and(table.GOODS_ID.eq(goodsId))
                .fetchOptionalInto(PersonalGoodsEntity.class);
    }

    @Override
    public Optional<PersonalGoodsEntity> saveOrUpdate(PersonalGoodsEntity entity) {
        return dslContext
                .insertInto(table)
                .set(table.USER_ID, entity.getUserId())
                .set(table.GOODS_ID, entity.getGoodsId())
                .set(table.ACCEPTED, entity.getAccepted())
                .set(table.BRAND_ID, entity.getBrandId())
                .set(table.PREFERENCE_TYPE, entity.getPreferenceType() != null ?
                        entity.getPreferenceType() : null)
                .set(table.PREFERENCE_VALUE, entity.getPreferenceValue())
                .set(table.START_DATE, entity.getStartDate())
                .set(table.END_DATE, entity.getEndDate())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.GOODS_ID)
                .doUpdate()
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .set(table.ACCEPTED, coalesce(val(entity.getAccepted()), table.ACCEPTED))
                .set(table.BRAND_ID, coalesce(val(entity.getBrandId()), table.BRAND_ID))
                .set(table.PREFERENCE_TYPE, entity.getPreferenceType() != null ?
                        entity.getPreferenceType() : null)
                .set(table.START_DATE, coalesce(val(entity.getStartDate()), table.START_DATE))
                .set(table.END_DATE, coalesce(val(entity.getEndDate()), table.END_DATE))
                .returning()
                .fetchOptionalInto(PersonalGoodsEntity.class);
    }

    @Override
    public List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> collect) {
        var insert =
                dslContext.insertInto(table,
                        table.BRAND_ID,
                        table.END_DATE,
                        table.START_DATE,
                        table.GOODS_ID,
                        table.CREATED_AT,
                        table.PREFERENCE_TYPE,
                        table.PREFERENCE_VALUE,
                        table.USER_ID,
                        table.PRICE_UP,
                        table.PRICE_DOWN,
                        table.CALCULATION_METHOD
                );

        for (var rec : collect)
            insert = insert.values(
                    rec.getBrandId(),
                    rec.getEndDate(),
                    rec.getStartDate(),
                    rec.getGoodsId(),
                    TimeHelper.currentTime(),
                    rec.getPreferenceType() != null ?
                            rec.getPreferenceType() : null,
                    rec.getPreferenceValue(),
                    rec.getUserId(),
                    rec.getPriceUp(),
                    rec.getPriceDown(),
                    rec.getCalculationMethod() != null ?
                    rec.getCalculationMethod() : null
            );

        var insertStep = insert
                .onConflict(table.USER_ID, table.GOODS_ID)
                .doUpdate()
                .set(table.ACCEPTED, DSL.coalesce(table.as("excluded").ACCEPTED, table.ACCEPTED))
                .set(table.BRAND_ID, DSL.coalesce(table.as("excluded").BRAND_ID, table.BRAND_ID))
                .set(table.PREFERENCE_TYPE, DSL.coalesce(table.as("excluded").PREFERENCE_TYPE, table.PREFERENCE_TYPE))
                .set(table.START_DATE, DSL.coalesce(table.as("excluded").START_DATE, table.START_DATE))
                .set(table.END_DATE, DSL.coalesce(table.as("excluded").END_DATE, table.END_DATE))
                .set(table.PRICE_UP, DSL.coalesce(table.as("excluded").PRICE_UP, table.PRICE_UP))
                .set(table.PRICE_DOWN, DSL.coalesce(table.as("excluded").PRICE_DOWN, table.PRICE_DOWN))
                .set(table.CALCULATION_METHOD, DSL.coalesce(table.as("excluded").CALCULATION_METHOD, table.CALCULATION_METHOD))
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returningResult(table.fields());

        return insertStep
                .fetchInto(PersonalGoodsEntity.class);
    }
}
