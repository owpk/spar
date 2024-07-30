package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.CouponEmission;
import ru.sparural.engine.repositories.CouponEmissionsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.CouponEmissions;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class CouponEmissionsRepositoryImpl implements CouponEmissionsRepository {
    private final DSLContext dslContext;
    private final CouponEmissions table = CouponEmissions.COUPON_EMISSIONS;

    @Override
    public List<CouponEmission> fetch(int offset, int limit) {
        return dslContext
                .selectFrom(table)
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(CouponEmission.class);
    }

    @Override
    public Optional<CouponEmission> get(long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(CouponEmission.class);
    }

    @Override
    public Optional<CouponEmission> save(CouponEmission couponEmission) {
        return dslContext
                .insertInto(CouponEmissions.COUPON_EMISSIONS)
                .set(CouponEmissions.COUPON_EMISSIONS.TITLE, couponEmission.getTitle())
                .set(CouponEmissions.COUPON_EMISSIONS.START, couponEmission.getStart())
                .set(CouponEmissions.COUPON_EMISSIONS.IS_PUBLIC, couponEmission.getIsPublic())
                .set(CouponEmissions.COUPON_EMISSIONS.CREATED_AT, TimeHelper.currentTime())
                .onConflict(CouponEmissions.COUPON_EMISSIONS.ID)
                .doUpdate()
                .set(CouponEmissions.COUPON_EMISSIONS.TITLE, couponEmission.getTitle())
                .set(CouponEmissions.COUPON_EMISSIONS.START, couponEmission.getStart())
                .set(CouponEmissions.COUPON_EMISSIONS.IS_PUBLIC, couponEmission.getIsPublic())
                .set(CouponEmissions.COUPON_EMISSIONS.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(CouponEmission.class);
    }

    @Override
    public Optional<CouponEmission> getByTitle(String title) {
        return dslContext
                .selectFrom(table)
                .where(table.TITLE.eq(title))
                .fetchOptionalInto(CouponEmission.class);
    }

    @Override
    public Optional<CouponEmission> update(Long id, Long end, Boolean isPublic) {
        return dslContext.update(table)
                .set(table.IS_PUBLIC, coalesce(val(isPublic), table.IS_PUBLIC))
                .set(table.END, coalesce(val(end), table.END))
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.ID.eq(id))
                .returning().fetchOptionalInto(CouponEmission.class);
    }

    // TODO CASCADE
    @Override
    public Boolean delete(Long id) {
        return dslContext.deleteFrom(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }
}
