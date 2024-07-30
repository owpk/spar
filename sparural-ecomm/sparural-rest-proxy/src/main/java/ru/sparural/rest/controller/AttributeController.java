package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.MerchantAttributeCreateOrUpdateDto;
import ru.sparural.engine.api.dto.merchant.Attribute;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@EqualsAndHashCode
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/merchant-attributes", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "merchants")
public class AttributeController {
    private final SparuralKafkaRequestCreator restToKafkaService;

    private final KafkaTopics kafkaTopics;
    @GetMapping
    public DataResponse<List<Attribute>> list(@RequestParam(defaultValue = "0") Integer offset,
                                              @RequestParam(defaultValue = "30") Integer limit) {
        List<Attribute> responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-attributes/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<Attribute>>builder()
                .success(true)
                .data(responses)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    public DataResponse<Attribute> get(@PathVariable Long id) {
        Attribute response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-attributes/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<Attribute>builder()
                .success(true)
                .data(response)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<Attribute> create(@Valid @RequestBody DataRequest<MerchantAttributeCreateOrUpdateDto> restRequest) {
        Attribute data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-attributes/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<Attribute>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<Attribute> update(@PathVariable Long id,
                                          @Valid @RequestBody DataRequest<MerchantAttributeCreateOrUpdateDto> restRequest) {
        Attribute data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-attributes/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<Attribute>builder()
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
                .withAction("merchant-attributes/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();

    }

}
