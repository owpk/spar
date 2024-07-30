package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.socials.SocialDto;
import ru.sparural.engine.api.dto.socials.SocialSetDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.security.UserPrincipal;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/social", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "socials")
public class SocialController {

    private final SparuralKafkaRequestCreator restToKafka;
    private final KafkaTopics kafkaTopics;

    @PostMapping("/{social}/set")
    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    public DataResponse<EmptyObject> set(@ApiIgnore UserPrincipal userPrincipal,
                                         @RequestBody SocialSetDto socialSetDto,
                                         @PathVariable String social) {
        Boolean response = restToKafka.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("social/set")
                .withRequestBody(socialSetDto)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("social", social)
                .sendForEntity();
        return DataResponse.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @PostMapping("/{social}/remove")
    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    public DataResponse<EmptyObject> delete(@ApiIgnore UserPrincipal userPrincipal,
                                            @PathVariable String social) {
        Boolean response = restToKafka.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("social/remove")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("social", social)
                .sendForEntity();
        return DataResponse.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @GetMapping("/binding")
    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    public DataResponse<List<SocialDto>> getSocialsBind(@ApiIgnore UserPrincipal userPrincipal) {
        List<SocialDto> response = restToKafka.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("social/get-binding")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<List<SocialDto>>builder()
                .data(response)
                .success(true)
                .build();
    }
}