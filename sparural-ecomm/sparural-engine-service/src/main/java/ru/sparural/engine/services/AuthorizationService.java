package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.user.CodeLoginResponse;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.kafka.exception.KafkaControllerException;

/**
 * @author Vorobyev Vyacheslav
 */
public interface AuthorizationService {
    TokenDataDto authorizeViaSocials(String code, String socialsName) throws ResourceNotFoundException, KafkaControllerException;

    TokenDataDto authorizeUser(String logingData, String password)
            throws UnauthorizedException, ResourceNotFoundException, ValidationException, KafkaControllerException;

    TokenDataDto generateUserRolesResponse(UserDto user);

    CodeLoginResponse exchangeForOneTimeCode(String login);

    TokenDataDto loginViaOneTimeCode(String token, String code);
}
