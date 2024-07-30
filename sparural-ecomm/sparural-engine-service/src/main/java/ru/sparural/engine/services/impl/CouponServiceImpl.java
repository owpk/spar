package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.CouponEmissionsDto;
import ru.sparural.engine.entity.Coupon;
import ru.sparural.engine.entity.CouponEmission;
import ru.sparural.engine.repositories.CouponRepository;
import ru.sparural.engine.services.CouponService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public Boolean checkIfCouponExists(String code) {
        return couponRepository.getByCode(code).isPresent();
    }


    @Override
    public CouponDto getWithEmission(Long id) {
        CouponDto dto = createDto(couponRepository.get(id)
                .orElse(null));
        if (dto != null) {
            CouponEmission couponEmission = couponRepository.insertEmissionToBody(createEntity(dto))
                    .orElse(null);
            if (couponEmission.getIsPublic() != null && couponEmission != null) {
                if (couponEmission.getIsPublic()) {
                    dto.setEmission(dtoMapperUtils.convert(couponEmission, CouponEmissionsDto.class));
                }
            }
        }
        return dto;
    }

    @Override
    public List<CouponDto> createListDto(List<Coupon> list) {
        return dtoMapperUtils.convertList(CouponDto.class, list);
    }

    @Override
    public CouponDto createDto(Coupon coupon) {
        return dtoMapperUtils.convert(coupon, CouponDto.class);
    }

    @Override
    public CouponDto save(CouponDto couponDto) {
        return createDto(couponRepository.save(createEntity(couponDto))
                .orElseThrow(() -> new ServiceException("Error creating coupon")));
    }

    @Override
    public Coupon createEntity(CouponDto couponDto) {
        return dtoMapperUtils.convert(couponDto, Coupon.class);
    }

    @Override
    public CouponDto getByCodeIfExist(String code) {
        return createDto(couponRepository.getByCode(code).orElseThrow(() -> new ServiceException("Error getting coupon")));
    }
}
