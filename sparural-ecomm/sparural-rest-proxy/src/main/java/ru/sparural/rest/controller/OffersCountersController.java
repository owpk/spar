package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.OffersCounterDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import ru.sparural.rest.config.KafkaTopics;


@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/offers-counters", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "offers-counters")
public class OffersCountersController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<OffersCounterDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                     @RequestParam(defaultValue = "30") Integer limit) {
        List<OffersCounterDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("offers-counters/index")
                .withRequestParameters(
                        Map.of(
                                "offset", offset,
                                "limit", limit))
                .sendForEntity();
        return DataResponse.<List<OffersCounterDto>>builder()
                .data(response)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<OffersCounterDto> get(@PathVariable Long id) {
        OffersCounterDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("offers-counters/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<OffersCounterDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<OffersCounterDto> create(@Valid @Parameter @RequestBody DataRequest<OffersCounterDto> restRequest) {
        OffersCounterDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("offers-counters/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<OffersCounterDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<OffersCounterDto> update(@Valid @Parameter @RequestBody DataRequest<OffersCounterDto> restRequest,
                                                  @PathVariable Long id) {
        OffersCounterDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("offers-counters/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<OffersCounterDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("offers-counters/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(response).build();
    }

}
