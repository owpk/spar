package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.RecoveryPasswordConfirmCodeRequestDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.RecoveryTokenResponseDto;
import ru.sparural.gobals.Constants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.services.AuthorizationService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.JwtResponse;
import ru.sparural.rest.security.TokenManager;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "recovery password")
public class RecoveryPasswordRequestController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final AuthorizationService authorizationService;
    private final KafkaTopics kafkaTopics;
    private final TokenManager tokenManager;

    @PostMapping("/recovery-password")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public RecoveryTokenResponseDto recoverPassword(@Valid @RequestBody RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        return authorizationService.recoverPassword(recoveryPasswordRequestDto);
    }

    @PostMapping("/recovery-password-confirm")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse confirmCode(@Valid @RequestBody RecoveryPasswordConfirmCodeRequestDto requestDto,
                                         HttpServletResponse response,
                                         @RequestHeader(Constants.CLIENT_TYPE_HEADER_NAME) String client) {

        var token = authorizationService.confirmPasswordCode(requestDto);
        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }
        return token;
    }
}