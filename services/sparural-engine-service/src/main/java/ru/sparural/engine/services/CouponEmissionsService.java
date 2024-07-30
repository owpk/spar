package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CouponEmissionsDto;
import ru.sparural.engine.api.dto.CouponEmissionsRequestDto;
import ru.sparural.engine.entity.CouponEmission;

import java.util.List;

public interface CouponEmissionsService {
    List<CouponEmissionsDto> getList(int offset, int limit);

    CouponEmissionsDto get(Long id);

    List<CouponEmissionsDto> createListDto(List<CouponEmission> list);

    CouponEmissionsDto createDto(CouponEmission couponEmission);

    CouponEmissionsDto save(CouponEmissionsDto couponEmissionsDto);

    CouponEmission createEntity(CouponEmissionsDto couponEmissionsDto);

    Boolean checkIfCouponEmissionExists(String title);

    CouponEmissionsDto getByTitle(String title);

    CouponEmissionsDto update(Long id, CouponEmissionsRequestDto couponEmissionsDto);

    Boolean delete(Long id);
}
