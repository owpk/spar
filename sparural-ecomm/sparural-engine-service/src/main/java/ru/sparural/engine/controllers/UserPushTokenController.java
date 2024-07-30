package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.user.UserPushTokenDto;
import ru.sparural.engine.services.UserPushTokenService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class UserPushTokenController {
    private final UserPushTokenService userPushTokenService;
    private final DtoMapperUtils dtoMapperUtils;

    @KafkaSparuralMapping("push-tokens/get")
    public List<UserPushTokenDto> getAllByUserId(@RequestParam Long userId) {
        return dtoMapperUtils.convertList(UserPushTokenDto.class, userPushTokenService.getAllByUserId(userId));
    }

    @KafkaSparuralMapping("push-tokens/remove")
    public Boolean remove(@RequestParam String token) {
        userPushTokenService.deleteByToken(token);
        return true;
    }
}
