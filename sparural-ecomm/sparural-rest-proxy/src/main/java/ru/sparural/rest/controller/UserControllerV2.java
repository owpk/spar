package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.NotAllowedForAnonymous;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "${rest.base-url}/v2/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "users")
public class UserControllerV2 {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @NotAllowedForAnonymous
    @GetMapping
    @ResponseType(ControllerResponseType.WRAPPED)
    @ApiOperation(value = "get user profile", authorizations = {
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    public UserProfileDto getUserData(@ApiIgnore UserPrincipal principal) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/get")
                .withRequestParameter("userId", principal.getUserId())
                .sendForEntity();
    }
}
