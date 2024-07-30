package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminUpdateDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;

@EqualsAndHashCode
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/goods-by-ext-goods-id", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "goods")
public class GoodsByExtGoodsIdController {
    private final SparuralKafkaRequestCreator restToKafkaService;

    private final KafkaTopics kafkaTopics;

    @IsManagerOrAdmin
    @GetMapping("/{goodsId}")
    public DataResponse<GoodsForAdminDto> get(@PathVariable String goodsId) {
        GoodsForAdminDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods-by-ext-goods-id/get")
                .withRequestParameter("goodsId", goodsId)
                .sendForEntity();
        return DataResponse.<GoodsForAdminDto>builder()
                .success(true)
                .data(response)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{goodsId}")
    public DataResponse<GoodsForAdminDto> update(@PathVariable String goodsId,
                                                 @Valid @RequestBody DataRequest<GoodsForAdminUpdateDto> restRequest) {
        GoodsForAdminDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods-by-ext-goods-id/update")
                .withRequestParameter("goodsId", goodsId)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<GoodsForAdminDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable String id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods-by-ext-goods-id/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();

    }
}
