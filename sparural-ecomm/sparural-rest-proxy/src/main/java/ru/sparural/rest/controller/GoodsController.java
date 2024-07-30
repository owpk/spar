package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.goods.GoodsForAdminCreateDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminUpdateDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@EqualsAndHashCode
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/goods", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "goods")
public class GoodsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsManagerOrAdmin
    @GetMapping
    public DataResponse<List<GoodsForAdminDto>> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "30") Integer limit) throws UnsupportedEncodingException {
        List<GoodsForAdminDto> responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("search", search)
                .sendForEntity();
        return DataResponse.<List<GoodsForAdminDto>>builder()
                .success(true)
                .data(responses)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    public DataResponse<GoodsForAdminDto> get(@PathVariable Long id) {
        GoodsForAdminDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<GoodsForAdminDto>builder()
                .success(true)
                .data(response)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<GoodsForAdminDto> create(@Valid @RequestBody DataRequest<GoodsForAdminCreateDto> restRequest) {
        GoodsForAdminDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<GoodsForAdminDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<GoodsForAdminDto> update(@PathVariable Long id,
                                                 @Valid @RequestBody DataRequest<GoodsForAdminUpdateDto> restRequest) {
        GoodsForAdminDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods/update")
                .withRequestParameter("id", id)
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
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("goods/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();

    }

}
