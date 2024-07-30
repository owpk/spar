package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.settings.ConfirmCodeSettingDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsAdmin;

import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/confirm-code-settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "settings")
public class ConfirmCodeSettingsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsAdmin
    public DataResponse<ConfirmCodeSettingDto> get() {
        ConfirmCodeSettingDto dto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("confirm-code-settings/get")
                .sendForEntity();
        return DataResponse.<ConfirmCodeSettingDto>builder()
                .success(true)
                .data(dto)
                .build();
    }

    @PutMapping
    @IsAdmin
    public DataResponse<ConfirmCodeSettingDto> update(@Valid @RequestBody DataRequest<ConfirmCodeSettingDto> restRequest) {
        ConfirmCodeSettingDto dto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("confirm-code-settings/update")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<ConfirmCodeSettingDto>builder()
                .success(true)
                .data(dto)
                .build();
    }

}