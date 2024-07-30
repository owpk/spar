package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.screen.mycards.MyCardsInfoScreenDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import springfox.documentation.annotations.ApiIgnore;

@Deprecated
@RestController
@RequestMapping(value = "${rest.base-url}/v1/my-cards", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "cards")
public class MyCardsControllerV1 {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsClient
    public DataResponse<MyCardsInfoScreenDto> getMyCardsInfoV1(@ApiIgnore UserPrincipal userPrincipal) {
        MyCardsInfoScreenDto serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("my-cards/v1")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<MyCardsInfoScreenDto>builder()
                .success(true)
                .data(serviceResponse)
                .build();
    }
}
