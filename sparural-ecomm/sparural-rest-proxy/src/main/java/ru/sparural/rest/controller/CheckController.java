package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

import javax.validation.constraints.Max;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/checks", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "check")
public class CheckController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @GetMapping("/{id}")
    public DataResponse<CheckDto> get(@PathVariable Long id,
                                      @ApiIgnore UserPrincipal userPrincipal) {
        CheckDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("checks/get")
                .withRequestParameter("id", id)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<CheckDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsClient
    @GetMapping
    public DataResponse<List<CheckDto>> index(@ApiIgnore UserPrincipal userPrincipal,
                                              @RequestParam(defaultValue = "0") Integer offset,
                                              @RequestParam(defaultValue = "30") @Max(1000) Integer limit,
                                              @RequestParam Long cardId,
                                              @RequestParam Long dateStart,
                                              @RequestParam Long dateEnd) {
        dateEnd += 86400;
        List<CheckDto> data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("checks/index")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("cardId", cardId)
                .withRequestParameter("dateStart", dateStart)
                .withRequestParameter("dateEnd", dateEnd)
                .sendForEntity();
        return DataResponse.<List<CheckDto>>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();

    }
}
