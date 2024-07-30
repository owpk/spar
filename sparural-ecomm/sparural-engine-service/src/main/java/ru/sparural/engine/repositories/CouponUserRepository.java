package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.CouponUserEntity;

import java.util.List;
import java.util.Optional;

public interface CouponUserRepository {
    Optional<CouponUserEntity> checkIfCouponUserExist(Long userId, Long couponId);

    List<CouponUserEntity> getCouponUserList(Long userId);

    Optional<CouponUserEntity> saveOrUpdate(CouponUserEntity entity);
}
