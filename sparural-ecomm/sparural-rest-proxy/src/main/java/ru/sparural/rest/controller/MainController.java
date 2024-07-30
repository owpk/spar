package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.kafka.model.ServiceResponse;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@Deprecated
@RestController
@RequestMapping(value = "${rest.base-url}/v1/mobile-main", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "mobile-main")
public class MainController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<Object> getMainScreen(@RequestParam(required = false) String userLongitude,
                                              @RequestParam(required = false) String userLatitude,
                                              @ApiIgnore UserPrincipal userPrincipal) {
        Long userId = 0L;
        if (userPrincipal != null) {
            userId = userPrincipal.getUserId();
        }

        ServiceResponse serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userId)
                .withRequestParameter("userLongitude", userLongitude)
                .withRequestParameter("userLatitude", userLatitude)
                .sendForEntity();
        return DataResponse.<Object>builder()
                .success(true)
                .data(serviceResponse.getBody())
                .meta(serviceResponse.getMeta())
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping("/select-cards")
    public DataResponse<List<UserCardDto>> selectCards(@ApiIgnore UserPrincipal userPrincipal) {
        List<UserCardDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main/select-cards")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<List<UserCardDto>>builder()
                .success(true)
                .data(serviceResponse)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping("/category")
    public DataResponse<List<CategoryDto>> selectCategory(@ApiIgnore UserPrincipal userPrincipal,
                                                          @RequestParam(defaultValue = "0") Integer offset,
                                                          @RequestParam(defaultValue = "30") Integer limit) {
        List<CategoryDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main/select-cards")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<CategoryDto>>builder()
                .success(true)
                .data(serviceResponse)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping("/personal-goods")
    public DataResponse<List<PersonalGoodsDto>> selectPersonalGoods(@ApiIgnore UserPrincipal userPrincipal,
                                                                    @RequestParam(defaultValue = "0") Integer offset,
                                                                    @RequestParam(defaultValue = "3") Integer limit) {
        List<PersonalGoodsDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main/coupons")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<PersonalGoodsDto>>builder()
                .success(true)
                .data(serviceResponse)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping("/coupons")
    public DataResponse<List<CouponDto>> selectCoupons(@ApiIgnore UserPrincipal userPrincipal,
                                                       @RequestParam(defaultValue = "0") Integer offset,
                                                       @RequestParam(defaultValue = "3") Integer limit) {
        List<CouponDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main/coupons")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<CouponDto>>builder()
                .success(true)
                .data(serviceResponse)
                .version(Constants.VERSION)
                .build();
    }

    @GetMapping("/offers")
    public DataResponse<List<UserCardDto>> selectOffers(@RequestParam(defaultValue = "0") Integer offset,
                                                        @RequestParam(defaultValue = "3") Integer limit,
                                                        @ApiIgnore UserPrincipal userPrincipal) {
        List<UserCardDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main/offers")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("userId", userPrincipal != null ? userPrincipal.getUserId() : null)
                .sendForEntity();
        return DataResponse.<List<UserCardDto>>builder()
                .success(true)
                .data(serviceResponse)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping("/personal-offers")
    public DataResponse<List<PersonalOfferDto>> selectPersOffers(@ApiIgnore UserPrincipal userPrincipal,
                                                                 @RequestParam(defaultValue = "0") Integer offset,
                                                                 @RequestParam(defaultValue = "3") Integer limit) {
        List<PersonalOfferDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("mobile-main/personal-offers")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<PersonalOfferDto>>builder()
                .success(true)
                .data(serviceResponse)
                .version(Constants.VERSION)
                .build();
    }
}