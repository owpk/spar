package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.MainBlockDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/main-blocks", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "main block")
public class MainBlockController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<List<MainBlockDto>> get(@RequestParam(defaultValue = "0") Integer offset,
                                                @RequestParam(defaultValue = "30") Integer limit) {
        List<MainBlockDto> list = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("main-blocks/list")
                .withRequestParameters(Map.of("offset", offset, "limit", limit))
                .sendForEntity();
        return new DataResponse<>(list);
    }

    @PutMapping("/{code}")
    @IsManagerOrAdmin
    public DataResponse<MainBlockDto> update(@Valid @Parameter @RequestBody DataRequest<MainBlockDto> restRequest,
                                             @PathVariable String code) {
        MainBlockDto mainBlockDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("code", code)
                .withAction("main-blocks/update")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return new DataResponse<>(mainBlockDto);
    }
}