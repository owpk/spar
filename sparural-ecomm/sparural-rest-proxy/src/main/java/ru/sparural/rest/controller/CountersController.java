package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.counters.CounterDto;
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
@RequestMapping(value = "${rest.base-url}/${rest.version}/counters", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "counters")
public class CountersController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<CounterDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                               @RequestParam(defaultValue = "30") Integer limit) {
        List<CounterDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("counters/index")
                .withRequestParameters(
                        Map.of(
                                "offset", offset,
                                "limit", limit))
                .sendForEntity();
        return DataResponse.<List<CounterDto>>builder()
                .data(response)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<CounterDto> get(@PathVariable Long id) {
        CounterDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("counters/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<CounterDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<CounterDto> create(@Valid @Parameter @RequestBody DataRequest<CounterDto> restRequest) {
        CounterDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("counters/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<CounterDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<CounterDto> update(@Valid @Parameter @RequestBody DataRequest<CounterDto> restRequest,
                                                  @PathVariable Long id) {
        CounterDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("counters/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<CounterDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("counters/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(response).build();
    }

}
