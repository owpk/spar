package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CouponUserDto;
import ru.sparural.engine.entity.CouponUserEntity;
import ru.sparural.engine.repositories.CouponUserRepository;
import ru.sparural.engine.services.CouponUserService;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponUserServiceImpl implements CouponUserService {

    private final DtoMapperUtils dtoMapperUtils;
    private final CouponUserRepository couponUserRepository;

    @Override
    public Boolean checkIfCouponExists(Long userId, Long couponId) {
        CouponUserEntity entity = couponUserRepository.checkIfCouponUserExist(userId, couponId)
                .orElse(null);
        return entity != null;

    }

    @Override
    public List<CouponUserDto> getList(Long userId) {
        return createListDto(couponUserRepository.getCouponUserList(userId));
    }

    @Override
    public List<CouponUserDto> createListDto(List<CouponUserEntity> list) {
        return dtoMapperUtils.convertList(CouponUserDto.class, list);
    }

    @Override
    public CouponUserDto createDto(CouponUserEntity entity) {
        return dtoMapperUtils.convert(entity, CouponUserDto.class);
    }

    @Override
    public CouponUserDto save(CouponUserDto dto) {
        CouponUserEntity entity = couponUserRepository.saveOrUpdate(createEntity(dto))
                .orElse(null);
        if (entity != null) {
            return createDto(entity);
        }
        return null;
    }

    @Override
    public CouponUserEntity createEntity(CouponUserDto dto) {
        return dtoMapperUtils.convert(dto, CouponUserEntity.class);
    }
}
