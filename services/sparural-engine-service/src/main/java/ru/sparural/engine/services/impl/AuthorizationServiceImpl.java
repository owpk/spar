package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.sparural.engine.api.dto.user.CodeLoginResponse;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.entity.Role;
import ru.sparural.engine.entity.RoleNames;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.rest.dto.TokenExchangeResponse;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.services.impl.LoymaxServiceImpl;
import ru.sparural.engine.services.AuthorizationService;
import ru.sparural.engine.services.RoleService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.utils.ReflectUtils;

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
                    .createLoymaxUser(user.getId(), userInfo.getPersonUid(), tokenExchangeResponse);
            loymaxService.setMobile(anotherLoymaxUser);
            loymaxService.saveOrUpdate(anotherLoymaxUser);
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
        return saveUserAndGenerateLoginData(loymaxResponse, null);
    }

    private TokenDataDto processPhoneRequest(String phone, String secret) {
        try {
            var user = userService.findByPhone(phone);
            return authorizeWithRoles(user, phone, secret);
        } catch (ResourceNotFoundException | UserNotFoundException e) {
            var user = new User();
            user.setPhoneNumber(phone);
            return processIfNotExists(phone, secret);
        }
    }

    private TokenDataDto processEmailRequest(String email, String secret) {
        try {
            var user = userService.findByEmail(email);
            return authorizeWithRoles(user, email, secret);
        } catch (ResourceNotFoundException | UserNotFoundException e) {
            var user = new User();
            user.setEmail(email);
            return processIfNotExists(email, secret);
        }
    }

    private TokenDataDto authorizeWithRoles(User user, String login, String password) {
        var roles = user.getRoles();
        if (roles != null && roles.size() != 0) {
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

    private TokenDataDto processForLoymax(User user, String login, String secret) {
        TokenExchangeResponse tokenResponse = tokenExchangeResponse(login, secret);
        var loymaxUserInfo = loymaxService.getUserInfo(tokenResponse.getAccessToken());

        saveLoymaxUser(user, loymaxUserInfo, tokenResponse);

        var u = userService.mapLoymaxInfoToUser(loymaxUserInfo);
        ReflectUtils.updateNotNullFields(u, user);
        if (userService.checkIfUserExistsWithPhoneOrEmail(user.getPhoneNumber(), user.getEmail()))
            userService.update(user);
        var dto = userService.createDto(user);
        return generateUserRolesResponse(dto);
    }

    private void saveLoymaxUser(User user, LoymaxUserInfo loymaxUserInfo, TokenExchangeResponse tokenResponse) {
        var loymaxUser = loymaxService.createLoymaxUser(
                user.getId(), loymaxUserInfo.getPersonUid(), tokenResponse);
        loymaxUser.setPersonUid(loymaxUserInfo.getPersonUid());
        loymaxUser.setId(loymaxUserInfo.getId());
        loymaxService.setMobile(loymaxUser);
        loymaxService.saveOrUpdate(loymaxUser);
    }

    private TokenDataDto processIfNotExists(String login, String secret) {
        var tokenResponse = tokenExchangeResponse(login, secret);
        return saveUserAndGenerateLoginData(tokenResponse, secret);
    }

    private TokenDataDto saveUserAndGenerateLoginData(TokenExchangeResponse tokenResponse, String secret) {
        var loymaxUserInfo = loymaxService.getUserInfo(tokenResponse.getAccessToken());
        var phone = loymaxService.getPhoneNumber(tokenResponse.getAccessToken());
        var u = userService.createFromLoymaxData(
                loymaxUserInfo,
                phone,
                secret != null ? bCryptPasswordEncoder.encode(secret) : null,
                List.of(roleService.getByName(RoleNames.CLIENT.getName())));
        u.setPhoneNumber(phone);

        try {
            User founded = userService.findByEmailOrPhone(phone, loymaxUserInfo.getEmail());
            founded.setPhoneNumber(null);
            founded.setEmail(null);
            userService.update(founded);
        } catch (ResourceNotFoundException e) {
            // ignore, needed only for check if user exists
        }

        User anotherUser = userService.saveOrUpdate(u);
        saveLoymaxUser(anotherUser, loymaxUserInfo, tokenResponse);
        return generateUserRolesResponse(userService.createDto(anotherUser));
    }

    private TokenExchangeResponse tokenExchangeResponse(String login, String secret) {
        return loymaxService.exchangeForToken(login, secret);
    }

}