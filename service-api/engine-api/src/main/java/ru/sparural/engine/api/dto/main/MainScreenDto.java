package ru.sparural.engine.api.dto.main;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MainScreenDto {
    List<UserCardDto> myCards;

    List<CategoryDto> favoriteCategories;

    List<CouponDto> coupones;

    List<OfferDto> offers;

    List<PersonalOfferDto> personalOffers;

    List<PersonalGoodsDto> personalGoods;

    List<CatalogDto> catalogs;

    MapDto map;
}