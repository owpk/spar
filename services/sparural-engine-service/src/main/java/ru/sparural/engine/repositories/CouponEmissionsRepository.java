package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.CouponEmission;

import java.util.List;
import java.util.Optional;

public interface CouponEmissionsRepository {
    List<CouponEmission> fetch(int offset, int limit);

    Optional<CouponEmission> get(long id);

    Optional<CouponEmission> save(CouponEmission couponEmission);

    Optional<CouponEmission> getByTitle(String title);

    Optional<CouponEmission> update(Long id, Long end, Boolean isPublic);

    Boolean delete(Long id);
}
