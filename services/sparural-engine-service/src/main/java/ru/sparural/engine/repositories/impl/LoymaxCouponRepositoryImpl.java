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
                .set(table.COUPONID, couponId)
                .set(table.LOYMAXCOUPONID, loymaxCouponId)
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.COUPONID, table.LOYMAXCOUPONID)
                .doUpdate()
                .set(table.LOYMAXCOUPONID, loymaxCouponId)
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public Optional<LoymaxCoupon> findByCouponIdAndLoymaxCouponId(Long couponId, Long loymaxCouponId) {
        return dslContext
                .selectFrom(table)
                .where(table.COUPONID.eq(couponId))
                .and(table.LOYMAXCOUPONID.eq(loymaxCouponId))
                .fetchOptionalInto(LoymaxCoupon.class);
    }


}
