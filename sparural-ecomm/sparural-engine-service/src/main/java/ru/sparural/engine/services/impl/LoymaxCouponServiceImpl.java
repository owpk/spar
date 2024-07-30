package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxCoupon;
import ru.sparural.engine.loymax.rest.dto.LoymaxCouponsDto;
import ru.sparural.engine.repositories.LoymaxCouponRepository;
import ru.sparural.engine.services.LoymaxCouponService;
import ru.sparural.engine.utils.DtoMapperUtils;

@Service
@RequiredArgsConstructor
public class LoymaxCouponServiceImpl implements LoymaxCouponService {
    private final LoymaxCouponRepository loymaxCouponRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public Boolean checkIfLoymaxCouponExist(Long couponId, Long loymaxCouponId) {
        return loymaxCouponRepository.findByCouponIdAndLoymaxCouponId(couponId, loymaxCouponId).isPresent();
    }

    @Override
    public void save(Long couponId, Long loymaxCouponId) {
        loymaxCouponRepository.save(couponId, loymaxCouponId);
    }

    @Override
    public LoymaxCoupon createEntityFromDTO(LoymaxCouponsDto loymaxCouponsDto) {
        return dtoMapperUtils.convert(loymaxCouponsDto, LoymaxCoupon.class);
    }

    @Override
    public LoymaxCouponsDto createDTOFromEntity(LoymaxCoupon loymaxCoupon) {
        return dtoMapperUtils.convert(loymaxCoupon, LoymaxCouponsDto.class);
    }
}
