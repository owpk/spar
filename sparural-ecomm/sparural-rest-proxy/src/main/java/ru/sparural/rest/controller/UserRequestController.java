package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.UserRequestsDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/user-request", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "user-requests")
public class UserRequestController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @PostMapping
    DataResponse<UserRequestsDto> create(@ApiIgnore UserPrincipal userPrincipal,
                                         @Valid @RequestBody DataRequest<UserRequestsDto> userRequestsDto) {
        UserRequestsDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-request/create")
                .withRequestBody(userRequestsDto.getData())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<UserRequestsDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @PutMapping("/{id}")
    DataResponse<UserRequestsDto> update(@PathVariable Long id,
                                         @ApiIgnore UserPrincipal userPrincipal,
                                         @Valid @RequestBody DataRequest<UserRequestsDto> restRequest) {

        UserRequestsDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-request/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<UserRequestsDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

}
