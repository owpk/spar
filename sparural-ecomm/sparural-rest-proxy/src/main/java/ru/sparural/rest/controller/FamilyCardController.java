package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/family-cards",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "cards")
public class FamilyCardController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @GetMapping
    public DataResponse<List<UserCardDto>> getFamilyCard(@ApiIgnore UserPrincipal userPrincipal,
                                                         @RequestParam Long offset,
                                                         @RequestParam Long limit) {
        List<UserCardDto> serviceResponse = restToKafkaService.createRequestBuilder()
                .withAction("family-cards/index")
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<UserCardDto>>builder()
                .data(serviceResponse)
                .build();
    }
}