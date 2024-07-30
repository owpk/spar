package ru.sparural.engine.repositories.impl;


import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Coupon;
import ru.sparural.engine.entity.CouponEmission;
import ru.sparural.engine.repositories.CouponRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.CouponStates;
import ru.sparural.tables.CouponEmissions;
import ru.sparural.tables.Coupons;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final DSLContext dslContext;
    private final Coupons table = Coupons.COUPONS;


    @Override
    public Optional<Coupon> getByCode(String code) {
        return dslContext
                .selectFrom(table)
                .where(table.CODE.eq(code))
                .fetchOptionalInto(Coupon.class);
    }

    @Override
    public Optional<Coupon> save(Coupon coupon) {
        return dslContext
                .insertInto(table)
                .set(table.CODE, coupon.getCode())
                .set(table.QRCONTENT, coupon.getQrContent())
                .set(table.COUPONEMMISSIONSID, coupon.getCouponEmmissionsId())
                .set(table.COUPONSTATE, coupon.getCouponState() != null ?
                        CouponStates.valueOf(coupon.getCouponState()) : CouponStates.Issued)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.CODE)
                .doUpdate()
                .set(table.QRCONTENT, coupon.getQrContent())
                .set(table.COUPONEMMISSIONSID, coupon.getCouponEmmissionsId())
                .set(table.COUPONSTATE, coupon.getCouponState() != null ?
                        CouponStates.valueOf(coupon.getCouponState()) : CouponStates.Issued)
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Coupon.class);
    }

    @Override
    public Optional<CouponEmission> insertEmissionToBody(Coupon date) {
        return dslContext
                .selectFrom(CouponEmissions.COUPON_EMISSIONS)
                .where(CouponEmissions.COUPON_EMISSIONS.ID.eq(date.getCouponEmmissionsId()))
                .fetchOptionalInto(CouponEmission.class);
    }

    @Override
    public Optional<Coupon> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(Coupon.class);

    }
}

