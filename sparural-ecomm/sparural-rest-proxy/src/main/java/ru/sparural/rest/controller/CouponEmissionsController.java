package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.CouponEmissionsDto;
import ru.sparural.engine.api.dto.CouponEmissionsRequestDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/coupon-emmissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "coupons")
public class CouponEmissionsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsManagerOrAdmin
    @GetMapping
    public DataResponse<List<CouponEmissionsDto>> get(@RequestParam(defaultValue = "0") Integer offset,
                                                      @RequestParam(defaultValue = "30") Integer limit) {
        List<CouponEmissionsDto> list = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("coupon-emmissions/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<CouponEmissionsDto>>builder()
                .success(true)
                .data(list)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    public DataResponse<CouponEmissionsDto> get(@PathVariable Long id) {
        CouponEmissionsDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("coupon-emmissions/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<CouponEmissionsDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<CouponEmissionsDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody DataRequest<CouponEmissionsRequestDto> restRequest) {
        CouponEmissionsDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("coupon-emissions/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<CouponEmissionsDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("coupon-emissions/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();
    }

}
