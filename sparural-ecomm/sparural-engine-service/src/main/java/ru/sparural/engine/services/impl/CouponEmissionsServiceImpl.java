package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CouponEmissionsDto;
import ru.sparural.engine.api.dto.CouponEmissionsRequestDto;
import ru.sparural.engine.entity.CouponEmission;
import ru.sparural.engine.repositories.CouponEmissionsRepository;
import ru.sparural.engine.services.CouponEmissionsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponEmissionsServiceImpl implements CouponEmissionsService {
    private final CouponEmissionsRepository couponEmissionsRepository;
    private final DtoMapperUtils dtoMapperUtils;

    //TODO: add photo
    @Override
    public List<CouponEmissionsDto> getList(int offset, int limit) {
        return createListDto(couponEmissionsRepository.fetch(offset, limit));
    }

    @Override
    public CouponEmissionsDto get(Long id) {
        return createDto(couponEmissionsRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public List<CouponEmissionsDto> createListDto(List<CouponEmission> list) {
        return dtoMapperUtils.convertList(CouponEmissionsDto.class, () -> list);
    }

    @Override
    public CouponEmissionsDto createDto(CouponEmission couponEmission) {
        return dtoMapperUtils.convert(CouponEmissionsDto.class, () -> couponEmission);
    }

    @Override
    public CouponEmissionsDto save(CouponEmissionsDto couponEmissionsDto) {
        return createDto(couponEmissionsRepository.save(createEntity(couponEmissionsDto)).orElseThrow(
                () -> new ServiceException("Failed to create coupon")));
    }

    @Override
    public CouponEmission createEntity(CouponEmissionsDto couponEmissionsDto) {
        return dtoMapperUtils.convert(couponEmissionsDto, CouponEmission.class);
    }

    @Override
    public Boolean checkIfCouponEmissionExists(String title) {
        return couponEmissionsRepository.getByTitle(title).isPresent();
    }

    @Override
    public CouponEmissionsDto getByTitle(String title) {
        return createDto(couponEmissionsRepository.getByTitle(title).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public CouponEmissionsDto update(Long id, CouponEmissionsRequestDto couponEmissionsDto) {
        return createDto(couponEmissionsRepository.update(id, couponEmissionsDto.getEnd(), couponEmissionsDto.getIsPublic())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Boolean delete(Long id) {
        couponEmissionsRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return couponEmissionsRepository.delete(id);
    }

}
