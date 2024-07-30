package ru.sparural.engine.services;


import ru.sparural.engine.entity.LoymaxCoupon;
import ru.sparural.engine.loymax.rest.dto.LoymaxCouponsDto;


public interface LoymaxCouponService {
    Boolean checkIfLoymaxCouponExist(Long couponId, Long loymaxCouponId);

    void save(Long couponId, Long loymaxCouponIdo);

    LoymaxCoupon createEntityFromDTO(LoymaxCouponsDto loymaxCouponsDto);

    LoymaxCouponsDto createDTOFromEntity(LoymaxCoupon loymaxCoupon);
}
