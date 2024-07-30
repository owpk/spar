package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.dto.main.MainScreenDto;
import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MobileMainService {

    MainScreenDto get(@RequestParam String userLongitude,
                      @RequestParam String userLatitude,
                      @RequestParam Long userId) throws ExecutionException, InterruptedException;

    List<UserCardDto> selectCards(Long userId);

    List<CategoryDto> selectCategory(Long userId, Integer offset, Integer limit);

    List<PersonalGoodsDto> selectPersonalGoods(Long userId, Integer offset, Integer limit);

    List<CouponDto> selectCoupons(Long userId, Integer offset, Integer limit);

    List<OfferDto> selectOffers(Integer offset, Integer limit, Long userId);

    List<PersonalOfferDto> selectPersOffers(Long userId, Integer offset, Integer limit);
}