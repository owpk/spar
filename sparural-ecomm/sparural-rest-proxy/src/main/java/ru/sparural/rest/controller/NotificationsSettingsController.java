package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.NotificationDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/notifications-settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "settings")
public class NotificationsSettingsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<NotificationDto> get() {
        NotificationDto notificationDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications-settings/get")
                .sendForEntity();
        return new DataResponse<>(notificationDto);
    }

    @PutMapping
    @IsManagerOrAdmin
    public DataResponse<NotificationDto> update(@Valid @Parameter @RequestBody DataRequest<NotificationDto> restRequest) {
        NotificationDto notificationDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications-settings/update")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return new DataResponse<>(notificationDto);
    }

}