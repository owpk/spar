package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.engine.services.MainBlockService;
import ru.sparural.engine.services.MobileMainService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.kafka.model.ServiceResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
@Deprecated
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class MobileMainController {

    private final MobileMainService mobileMainService;
    private final MainBlockService mainBlocks;

    @KafkaSparuralMapping("mobile-main")
    public ServiceResponse get(@RequestParam String userLongitude,
                               @RequestParam String userLatitude,
                               @RequestParam Long userId) throws ExecutionException, InterruptedException {

        var meta = new ru.sparural.engine.api.dto.main.MetaDto();
        meta.setMainBlocks(mainBlocks.list(0, 30));

        var body = mobileMainService.get(userLongitude, userLatitude, userId);

        var serviceResponse = new ServiceResponse();
        serviceResponse.setBody(body);
        serviceResponse.setMeta(meta);
        return serviceResponse;
    }

    @KafkaSparuralMapping("mobile-main/select-cards")
    List<UserCardDto> selectCards(@RequestParam Long userId) {
        return mobileMainService.selectCards(userId);
    }

    @KafkaSparuralMapping("mobile-main/category")
    List<CategoryDto> selectCategory(@RequestParam Long userId,
                                     @RequestParam Integer offset,
                                     @RequestParam Integer limit) {
        return mobileMainService.selectCategory(userId, offset, limit);
    }

    @KafkaSparuralMapping("mobile-main/personal-goods")
    List<PersonalGoodsDto> selectPersonalGoods(@RequestParam Long userId,
                                               @RequestParam Integer offset,
                                               @RequestParam Integer limit) {
        return mobileMainService.selectPersonalGoods(userId, offset, limit);
    }

    @KafkaSparuralMapping("mobile-main/coupons")
    List<CouponDto> selectCoupons(@RequestParam Long userId,
                                  @RequestParam Integer offset,
                                  @RequestParam Integer limit) {
        return mobileMainService.selectCoupons(userId, offset, limit);
    }

    @KafkaSparuralMapping("mobile-main/offers")
    List<OfferDto> selectOffers(@RequestParam Integer offset,
                                @RequestParam Integer limit, @RequestParam Long userId) {
        return mobileMainService.selectOffers(offset, limit, userId);
    }

    @KafkaSparuralMapping("mobile-main/personal-offers")
    List<PersonalOfferDto> selectPersOffers(@RequestParam Long userId,
                                            @RequestParam Integer offset,
                                            @RequestParam Integer limit) {
        return mobileMainService.selectPersOffers(userId, offset, limit);
    }
}