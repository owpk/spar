package ru.sparural.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.NotificationsDto;
import ru.sparural.engine.api.dto.NotificationsListWithMetaDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.stream.Collectors;
import ru.sparural.rest.config.KafkaTopics;

@Deprecated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/${rest.version}/notifications", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags = "notifications")
public class NotificationsListControllerV1 {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Secured({RolesConstants.ROLE_CLIENT, RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_MANAGER})
    public DataResponse<List<NotificationsDto>> list(@ApiIgnore UserPrincipal userPrincipal,
                                                     @RequestParam(defaultValue = "0") Integer offset,
                                                     @RequestParam(defaultValue = "30") Integer limit,
                                                     @RequestParam(required = false) Boolean isReaded) throws JsonProcessingException {
        NotificationsListWithMetaDto responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications/index")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("isReaded", isReaded)
                .withRequestParameter("type", List.of("push"))
                .sendForEntity();

        var data = responses.getData().stream().map(fullDto -> {
            NotificationsDto dto = new NotificationsDto();
            dto.setBody(fullDto.getBody());
            dto.setId(fullDto.getId());
            dto.setIsReaded(fullDto.getIsReaded());
            dto.setMerchantId(fullDto.getMerchant() != null ? fullDto.getMerchant().getId() : null);
            dto.setScreenId(fullDto.getScreen() != null ? fullDto.getScreen().getId() : null);
            dto.setSendedAt(fullDto.getSendedAt());
            dto.setTitle(fullDto.getTitle());
            dto.setType(null);
            dto.setUserId(fullDto.getUserId());
            return dto;
        }).collect(Collectors.toList());

        return DataResponse.<List<NotificationsDto>>builder()
                .success(true)
                .data(data)
                .meta(responses.getMeta())
                .version(Constants.VERSION)
                .build();
    }

    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @PostMapping("{id}/read")
    DataResponse<NotificationsDto> update(@ApiIgnore UserPrincipal userPrincipal,
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
