package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/accounts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "accounts")
public class AccountsLifeTimesByTimeController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsClient
    @GetMapping("/{id}/account-life-time-by-time")
    public DataResponse<List<AccountsLifeTimesByTimeDTO>> get(@ApiIgnore UserPrincipal userPrincipal,
                                                              @PathVariable Long id,
                                                              @RequestParam(defaultValue = "0") Integer offset,
                                                              @RequestParam(defaultValue = "30") Integer limit) {
        List<AccountsLifeTimesByTimeDTO> accountsLifeTimesByTimeDTO = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("accounts/account-life-time-by-time")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("id", id)
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return new DataResponse<>(accountsLifeTimesByTimeDTO);
    }


}
