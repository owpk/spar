package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.UserDeviceDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/users-devices", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "users")
public class UserDeviceController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @PostMapping("/{identifier}")
    public DataResponse<UserDeviceDto> get(@Parameter @RequestBody DataRequest<UserDeviceDto> restRequest,
                                           @PathVariable String identifier,
                                           @ApiIgnore UserPrincipal principal) {
        UserDeviceDto userDeviceDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-devices/save")
                .withRequestParameters(Map.of("deviceIdentifier", identifier, "userId", principal.getUserId()))
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return new DataResponse<>(userDeviceDto);
    }

    @IsClient
    @GetMapping
    public DataResponse<UserDeviceDto> get(@Parameter @RequestBody DataRequest<UserDeviceDto> restRequest) {
        UserDeviceDto userDeviceDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-device/get")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return new DataResponse<>(userDeviceDto);
    }

}