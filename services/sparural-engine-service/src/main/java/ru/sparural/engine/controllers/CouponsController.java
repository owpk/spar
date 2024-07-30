package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.CouponEmissionsDto;
import ru.sparural.engine.api.dto.CouponEmissionsRequestDto;
import ru.sparural.engine.api.dto.CouponUserDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.loymax.rest.dto.LoymaxCouponsDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.services.CouponEmissionsService;
import ru.sparural.engine.services.CouponService;
import ru.sparural.engine.services.CouponUserService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.LoymaxCouponService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
@Slf4j
public class CouponsController {

    private final LoymaxService loymaxService;
    private final CouponEmissionsService couponEmissionsService;
    private final CouponService couponService;
    private final LoymaxCouponService loymaxCouponService;
    private final CouponUserService couponUserService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("coupons/index")
    public List<CouponDto> list(@RequestParam Integer offset,
                                @RequestParam Integer limit,
                                @RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        //Get a list of coupons for a user from loymax
        List<LoymaxCouponsDto> loymaxCouponsDtoList = loymaxService.getCouponsList(loymaxUser);


        //Iterate over all Loymax coupons
        for (LoymaxCouponsDto x : loymaxCouponsDtoList) {
            //Coupon, fill in advance
            CouponEmissionsDto couponEmissionsDto = new CouponEmissionsDto();
            CouponDto coupon = new CouponDto();
            coupon.setCode(x.getCode());
            coupon.setQrContent(x.getQrContent());
            coupon.setCouponState(x.getCouponState());
            //If there is no such coupon in the published ones, then create it with the isPublic-false parameter
            if (!couponEmissionsService.checkIfCouponEmissionExists(x.getEmissionTitle())) {
                log.info(x.toString());
                //If there is no couponEmissionsDto -> no couponEmissionsDto, so save it
                couponEmissionsDto.setTitle(x.getEmissionTitle());
                couponEmissionsDto.setIsPublic(false);
                if (x.getEndDate() != null)
                    couponEmissionsDto.setEnd(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(x.getEndDate()));
                if (x.getCreateDate() != null)
                    couponEmissionsDto.setStart(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(x.getCreateDate()));
                coupon.setCouponEmmissionsId(couponEmissionsService.save(couponEmissionsDto).getId());
                couponService.save(coupon);


            } else {
                //If there is a couponEmissionsDto , check if there is a coupon, if not -> save it
                if (!couponService.checkIfCouponExists(x.getCode())) {
                    couponEmissionsDto = couponEmissionsService.getByTitle(x.getEmissionTitle());

                    CouponEmissionsRequestDto couponEmissionsRequestDto = new CouponEmissionsRequestDto();
                    if (x.getEndDate() != null)
                        couponEmissionsDto.setEnd(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(x.getEndDate()));
                    couponEmissionsService.update(couponEmissionsDto.getId(), couponEmissionsRequestDto);

                    coupon.setCouponEmmissionsId(couponEmissionsService.getByTitle(x.getEmissionTitle()).getId());
                    couponService.save(coupon);
                }
            }
            //We get the ID of the coupon, so if it is, you need to check if it is in Loymax coupons
            //if not -> save it
            Long couponId = couponService.getByCodeIfExist(x.getCode()).getId();
            if (!loymaxCouponService.checkIfLoymaxCouponExist(couponId, x.getId())) {
                loymaxCouponService.save(couponId, x.getId());
            }

            CouponDto couponDto = couponService.getByCodeIfExist(x.getCode());
            if (couponDto != null) {
                CouponUserDto couponUserDto = new CouponUserDto();
                couponUserDto.setCouponId(couponDto.getId());
                couponUserDto.setUserId(userId);
                couponUserService.save(couponUserDto);
            }

        }

        List<CouponUserDto> couponUserList = couponUserService.getList(userId);
        if (couponUserList == null) {
            return new ArrayList<>();
        }

        List<CouponDto> result = new ArrayList<>();
        int i = 0;
        for (CouponUserDto x : couponUserList) {
            CouponDto coupon = couponService.getWithEmission(x.getCouponId());
            if (i >= offset && result.size() < limit && coupon.getEmission() != null && coupon.getCouponState().equals("Issued")) {
                List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.COUPON_EMISSION_PHOTO, coupon.getEmission().getId());
                if (!files.isEmpty()) {
                    coupon.getEmission().setPhoto(files.get(files.size() - 1));
                }
                if (coupon.getEmission().getStart() <= Instant.now().getEpochSecond()) {
                    if (coupon.getEmission().getEnd() == null || coupon.getEmission().getEnd() >= Instant.now().getEpochSecond()) {
                        result.add(coupon);
                        i++;
                    }
                }
            }
        }
        return result;
    }

}
