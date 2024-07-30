package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.api.dto.registration.StepConstants;
import ru.sparural.engine.api.dto.user.CodeLoginResponse;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.entity.RoleNames;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.exceptions.LoymaxException;
import ru.sparural.engine.loymax.rest.dto.TokenExchangeResponse;
import ru.sparural.engine.loymax.services.impl.LoymaxServiceImpl;
import ru.sparural.engine.services.AuthorizationService;
import ru.sparural.engine.services.RegistrationsService;
import ru.sparural.engine.services.RoleService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.RegistrationStepException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServiceImpl implements AuthorizationService {
    private final LoymaxServiceImpl loymaxService;
    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RegistrationsService registrationsService;

    @Deprecated // by now...
    @Override
    public TokenDataDto authorizeViaSocials(String code, String socialsName) {
        var tokenExchangeResponse = loymaxService.loginViaSocial(code, socialsName);
        var userInfo = loymaxService.getUserInfo(
                tokenExchangeResponse.getAccessToken());
        try {
            var loymaxUser = loymaxService.findByPersonUid(userInfo.getPersonUid());
            var user = userService.findByUserId(loymaxUser.getUserId());
            return generateUserRolesResponse(userService.createDto(user));
        } catch (UserNotFoundException e) {
            var clientRole = roleService.getByName(RoleNames.CLIENT.getName());
            var user = userService.createFromLoymaxData(userInfo,
                    null, null, List.of(clientRole));
            var anotherUser = userService.saveOrUpdate(user);
            var anotherLoymaxUser = loymaxService
                    .createLoymaxUser(user.getId(), userInfo, tokenExchangeResponse);
            return generateUserRolesResponse(
                    userService.createDto(anotherUser));
        }
    }

    @Override
    public TokenDataDto authorizeUser(String logingData, String password) {
        if (logingData.matches("\\d+")) {
            return processPhoneRequest(logingData, password);
        } else if (logingData.matches(".+@.+")) {
            return processEmailRequest(logingData, password);
        } else throw new ValidationException(logingData);
    }

    @Override
    public TokenDataDto generateUserRolesResponse(UserDto user) {
        var tokenData = new TokenDataDto();
        tokenData.setRoles(user.getRoles());
        tokenData.setUserId(user.getId());
        String login = "";
        if (user.getEmail() != null)
            login = user.getEmail();
        else if (user.getPhoneNumber() != null)
            login = user.getPhoneNumber();
        tokenData.setLogin(login);
        log.info("response with token: {}", tokenData);
        return tokenData;
    }

    @Override
    public CodeLoginResponse exchangeForOneTimeCode(String login) {
        var loymaxCodeResponse = loymaxService.exchangeForOneTimePassword(login);
        var response = new CodeLoginResponse();
        response.setMessage(loymaxCodeResponse.getMessage());
        response.setTempLoginToken(loymaxCodeResponse.getCode());
        return response;
    }

    @Override
    public TokenDataDto loginViaOneTimeCode(String token, String code) {
        var loymaxResponse = loymaxService.loginViaOneTimeCode(token, code);
        return saveUserAndGenerateLoginData(loymaxResponse);
    }

    private TokenDataDto processPhoneRequest(String phone, String secret) {
        try {
            var user = userService.findByPhone(phone);
            return authorizeWithRoles(user, phone, secret);
        } catch (UserNotFoundException ignore) {
            var user = basicLoymaxLoginByPhone(phone, secret);
            return generateUserRolesResponse(userService.createDto(user));
        }
    }

    private User basicLoymaxLoginByPhone(String phone, String secret) {
        var token = loymaxService.exchangeForToken(phone, secret);
        var userData = loymaxService.getUserInfoSystem(phone);
        return registrationsService.createRegisteredUserIfLoymaxUserRegistered(userData, token);
    }

    private TokenDataDto processEmailRequest(String email, String secret) {
        var user = userService.findByEmail(email);
        return authorizeWithRoles(user, email, secret);
    }

    private TokenDataDto authorizeWithRoles(User user, String login, String password) {
        var roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            if (containsName(roles, RoleNames.ANONYMOUS.getName()))
                throw new UnauthorizedException(RoleNames.ANONYMOUS.getName() + " not allowed", 401);
            else if (containsName(roles, RoleNames.CLIENT.getName()))
                return processForLoymax(user, login, password);
            else if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                return generateUserRolesResponse(
                        userService.createDto(user));
            } else throw new UnauthorizedException("wrong password", 401);
        }
        throw new UnauthorizedException("cant create token response", 401);
    }

    private boolean containsName(final List<Role> list, final String name) {
        return list.stream().anyMatch(o -> o.getCode().equals(name));
    }

    private TokenDataDto processForLoymax(User user, String phone, String secret) {
        try {
            var dto = userService.createDto(basicLoymaxLoginByPhone(phone, secret));
            return generateUserRolesResponse(dto);
        } catch (LoymaxException e) {
            var currentStep = registrationsService.checkStep(user.getId());
            if (currentStep < StepConstants.COMPLETED.getStep())
                throw new RegistrationStepException(currentStep);
            else {
                userService.delete(user.getId());
                throw new RegistrationStepException(0);
            }
        }
    }

    @Transactional
    private TokenDataDto saveUserAndGenerateLoginData(TokenExchangeResponse tokenResponse) {
        var loymaxUserInfo = loymaxService.getUserInfo(tokenResponse.getAccessToken());
        var loymaxUserInfoSystem = loymaxService.getUserInfoSystemByLoymaxUserId(String.valueOf(loymaxUserInfo.getId()));
        var user = registrationsService.createRegisteredUserIfLoymaxUserRegistered(loymaxUserInfoSystem, tokenResponse);
        return generateUserRolesResponse(userService.createDto(user));
    }

    private TokenExchangeResponse tokenExchangeResponse(String login, String secret) {
        return loymaxService.exchangeForToken(login, secret);
    }

}