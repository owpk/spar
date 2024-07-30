package ru.sparural.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.NotificationFullDto;
import ru.sparural.engine.api.dto.NotificationsDto;
import ru.sparural.engine.api.dto.NotificationsListWithMetaDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/v2/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "notifications")
public class NotificationsListControllerV2 {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @Secured({RolesConstants.ROLE_CLIENT, RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_MANAGER})
    public DataResponse<List<NotificationFullDto>> list(@ApiIgnore UserPrincipal userPrincipal,
                                                            @RequestParam(defaultValue = "0") Integer offset,
                                                            @RequestParam(defaultValue = "30") Integer limit,
                                                            @RequestParam(required = false) Boolean isReaded,
                                                            @RequestParam(required = false) List<String> type) throws JsonProcessingException {
        NotificationsListWithMetaDto responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications/index")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("isReaded", isReaded)
                .withRequestParameter("type", type)
                .sendForEntity();
        return DataResponse.<List<NotificationFullDto>>builder()
                .success(true)
                .data(responses.getData())
                .meta(responses.getMeta())
                .version(Constants.VERSION)
                .build();
    }

    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @PostMapping("{id}/read")
    public DataResponse<NotificationsDto> update(@ApiIgnore UserPrincipal userPrincipal,
                                          @PathVariable Long id) {
        restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications/read")
                .withRequestParameter("id", id)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<NotificationsDto>builder()
                .success(true)
                .build();
    }
}

