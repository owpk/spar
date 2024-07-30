package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.user.CodeLoginResponse;
import ru.sparural.engine.api.dto.user.LoginUserDataRequestDto;
import ru.sparural.engine.api.dto.user.LoginViaOneTimeCodeUserDataDto;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.services.AuthorizationService;
import ru.sparural.engine.services.RegistrationsService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.kafka.exception.KafkaControllerException;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class LoginActionController {

    private final AuthorizationService authorizationService;
    private final UserService userService;

    @KafkaSparuralMapping("login")
    public TokenDataDto login(@Payload LoginUserDataRequestDto loginUserDataRequest) throws ValidationException, UnauthorizedException,
            ResourceNotFoundException, KafkaControllerException {
        return authorizationService
                .authorizeUser(loginUserDataRequest.getPhoneNumber(), loginUserDataRequest.getPassword());
    }

    @KafkaSparuralMapping("refresh-token")
    public TokenDataDto refresh(@Payload Long userId) throws UserNotFoundException {
        var user = userService.findByUserId(userId);
        return authorizationService.generateUserRolesResponse(
                userService.createDto(user));
    }

    @KafkaSparuralMapping("login/two-fa")
    public CodeLoginResponse exchangeForOneTimeCode(@RequestParam String login) {
        return authorizationService.exchangeForOneTimeCode(login);
    }

    @KafkaSparuralMapping("login/two-fa-confirm")
    public TokenDataDto loginViaOneTimeCode(@Payload LoginViaOneTimeCodeUserDataDto userDataDto) {
        return authorizationService.loginViaOneTimeCode(userDataDto.getTempToken(), userDataDto.getOneTimeCode());
    }

}
