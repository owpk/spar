package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.templates.MessageTypeDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/message-types", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "push-message-types")
public class MessageTypeController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<List<MessageTypeDto>> getAllPushMessageTypes() {
        List<MessageTypeDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("message-types/index")
                .sendForEntity();
        return DataResponse.<List<MessageTypeDto>>builder()
                .success(true)
                .data(serviceResponse)
                .build();
    }
}
