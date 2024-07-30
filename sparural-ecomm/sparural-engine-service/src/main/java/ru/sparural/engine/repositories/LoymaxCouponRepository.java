package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxCoupon;

import java.util.Optional;

public interface LoymaxCouponRepository {
    void save(Long couponId, Long loymaxCouponId);

    Optional<LoymaxCoupon> findByCouponIdAndLoymaxCouponId(Long couponId, Long loymaxCouponId);

}
