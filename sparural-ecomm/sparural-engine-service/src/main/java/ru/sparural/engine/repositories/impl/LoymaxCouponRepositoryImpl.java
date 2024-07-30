package ru.sparural.engine.repositories.impl;


import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxCoupon;
import ru.sparural.engine.repositories.LoymaxCouponRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxCoupons;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoymaxCouponRepositoryImpl implements LoymaxCouponRepository {

    private final DSLContext dslContext;
    private final LoymaxCoupons table = LoymaxCoupons.LOYMAX_COUPONS;


    @Override
    public void save(Long couponId, Long loymaxCouponId) {
        dslContext.insertInto(table)
                .set(table.COUPON_ID, couponId)
                .set(table.LOYMAX_COUPON_ID, loymaxCouponId)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.COUPON_ID, table.LOYMAX_COUPON_ID)
                .doUpdate()
                .set(table.LOYMAX_COUPON_ID, loymaxCouponId)
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public Optional<LoymaxCoupon> findByCouponIdAndLoymaxCouponId(Long couponId, Long loymaxCouponId) {
        return dslContext
                .selectFrom(table)
                .where(table.COUPON_ID.eq(couponId))
                .and(table.LOYMAX_COUPON_ID.eq(loymaxCouponId))
                .fetchOptionalInto(LoymaxCoupon.class);
    }


}
