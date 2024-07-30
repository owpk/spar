package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/accounts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "accounts")
public class AccountsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsClient
    public DataResponse<List<AccountsDto>> index(@ApiIgnore UserPrincipal userPrincipal,
                                                 @RequestParam(defaultValue = "0") Integer offset,
                                                 @RequestParam(defaultValue = "30") Integer limit) {
        List<AccountsDto> result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("accounts/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<AccountsDto>>builder()
                .success(true)
                .data(result)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/types")
    public DataResponse<List<AccountsTypeDto>> accountTypesList(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "30") Integer limit) {
        List<AccountsTypeDto> result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("accounts/types")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<AccountsTypeDto>>builder()
                .success(true)
                .data(result)
                .version(Constants.VERSION)
                .build();
    }
}
