package ru.sparural.rest.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.CouponDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/coupons",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "coupons")
public class CouponsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @GetMapping
    public DataResponse<List<CouponDto>> index(@RequestParam(defaultValue = "0") Integer offset,
                                               @RequestParam(defaultValue = "30") Integer limit,
                                               @ApiIgnore UserPrincipal userPrincipal) {
        List<CouponDto> data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("coupons/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<List<CouponDto>>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }
}
