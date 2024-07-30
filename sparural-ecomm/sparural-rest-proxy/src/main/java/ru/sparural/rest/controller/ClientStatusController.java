package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.ClientStatusDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@EqualsAndHashCode
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/client-statues", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "client status")
public class ClientStatusController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsManagerOrAdmin
    @GetMapping
    DataResponse<List<ClientStatusDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                             @RequestParam(defaultValue = "30") Integer limit) {

        List<ClientStatusDto> responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("client-statues/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<ClientStatusDto>>builder()
                .success(true)
                .data(responses)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    DataResponse<ClientStatusDto> get(@PathVariable Long id) {
        ClientStatusDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("client-statues/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<ClientStatusDto>builder()
                .success(true)
                .data(response)
                .version(Constants.VERSION)
                .build();
    }
}
