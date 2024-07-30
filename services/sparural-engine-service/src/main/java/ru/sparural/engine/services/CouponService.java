package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.entity.Coupon;

import java.util.List;

public interface CouponService {
    Boolean checkIfCouponExists(String code);

    CouponDto getWithEmission(Long id);

    List<CouponDto> createListDto(List<Coupon> list);

    CouponDto createDto(Coupon coupon);

    CouponDto save(CouponDto couponDto);

    Coupon createEntity(CouponDto couponDto);

    CouponDto getByCodeIfExist(String code);
}
