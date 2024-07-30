package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.PersonalGoodsEntity;
import ru.sparural.engine.repositories.PersonalGoodsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.PersonalGoodsPreferenceTypes;
import ru.sparural.tables.PersonalGoods;

import java.util.List;
import java.util.Optional;

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
                .where(table.USERID.eq(userId))
                .and(table.GOODSID.eq(goodsId))
                .fetchOptionalInto(PersonalGoodsEntity.class);
    }

    @Override
    public Optional<PersonalGoodsEntity> saveOrUpdate(PersonalGoodsEntity entity) {
        return dslContext
                .insertInto(table)
                .set(table.USERID, entity.getUserId())
                .set(table.GOODSID, entity.getGoodsId())
                .set(table.ACCEPTED, entity.getAccepted())
                .set(table.BRANDID, entity.getBrandId())
                .set(table.PREFERENCETYPE, entity.getPreferenceType() != null ?
                        PersonalGoodsPreferenceTypes.valueOf(entity.getPreferenceType()) : null)
                .set(table.PREFERENCEVALUE, entity.getPreferenceValue())
                .set(table.STARTDATE, entity.getStartDate())
                .set(table.ENDDATE, entity.getEndDate())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.USERID, table.GOODSID)
                .doUpdate()
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .set(table.ACCEPTED, coalesce(val(entity.getAccepted()), table.ACCEPTED))
                .set(table.BRANDID, coalesce(val(entity.getBrandId()), table.BRANDID))
                .set(table.PREFERENCETYPE, entity.getPreferenceType() != null ?
                        PersonalGoodsPreferenceTypes.valueOf(entity.getPreferenceType()) : null)
                .set(table.STARTDATE, coalesce(val(entity.getStartDate()), table.STARTDATE))
                .set(table.ENDDATE, coalesce(val(entity.getEndDate()), table.ENDDATE))
                .returning()
                .fetchOptionalInto(PersonalGoodsEntity.class);
    }

    @Override
    public List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> collect) {
        var insert =
                dslContext.insertInto(table, table.BRANDID, table.ENDDATE,
                        table.STARTDATE, table.GOODSID,
                        table.CREATEDAT, table.PREFERENCETYPE,
                        table.PREFERENCEVALUE, table.USERID,
                        table.PRICE_UP, table.PRICE_DOWN
                );

        for (var rec : collect)
            insert = insert.values(
                    rec.getBrandId(),
                    rec.getEndDate(),
                    rec.getStartDate(),
                    rec.getGoodsId(),
                    TimeHelper.currentTime(),
                    rec.getPreferenceType() != null ?
                            PersonalGoodsPreferenceTypes.valueOf(rec.getPreferenceType()) : null,
                    rec.getPreferenceValue(),
                    rec.getUserId(),
                    rec.getPriceUp(),
                    rec.getPriceDown()
            );

        var insertStep = insert
                .onConflict(table.USERID, table.GOODSID)
                .doUpdate()
                .set(table.ACCEPTED, DSL.coalesce(table.as("excluded").ACCEPTED, table.ACCEPTED))
                .set(table.BRANDID, DSL.coalesce(table.as("excluded").BRANDID, table.BRANDID))
                .set(table.PREFERENCETYPE, DSL.coalesce(table.as("excluded").PREFERENCETYPE, table.PREFERENCETYPE))
                .set(table.STARTDATE, DSL.coalesce(table.as("excluded").STARTDATE, table.STARTDATE))
                .set(table.ENDDATE, DSL.coalesce(table.as("excluded").ENDDATE, table.ENDDATE))
                .set(table.PRICE_UP, DSL.coalesce(table.as("excluded").PRICE_UP, table.PRICE_UP))
                .set(table.PRICE_DOWN, DSL.coalesce(table.as("excluded").PRICE_DOWN, table.PRICE_DOWN))
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returningResult(table.fields());

        return insertStep
                .fetchInto(PersonalGoodsEntity.class);
    }
}
