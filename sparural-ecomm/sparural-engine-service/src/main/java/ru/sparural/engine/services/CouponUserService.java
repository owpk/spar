package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CouponUserDto;
import ru.sparural.engine.entity.CouponUserEntity;

import java.util.List;

public interface CouponUserService {
    Boolean checkIfCouponExists(Long userId, Long couponId);

    List<CouponUserDto> getList(Long userId);

    List<CouponUserDto> createListDto(List<CouponUserEntity> list);

    CouponUserDto createDto(CouponUserEntity entity);

    CouponUserDto save(CouponUserDto dto);

    CouponUserEntity createEntity(CouponUserDto dto);
}
