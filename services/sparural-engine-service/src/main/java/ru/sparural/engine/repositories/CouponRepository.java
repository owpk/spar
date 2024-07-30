package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Coupon;
import ru.sparural.engine.entity.CouponEmission;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> getByCode(String code);

    Optional<Coupon> save(Coupon coupon);

    Optional<CouponEmission> insertEmissionToBody(Coupon data);

    Optional<Coupon> get(Long id);
}
