package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.user.CodeLoginResponse;
import ru.sparural.engine.api.dto.user.LoginUserDataRequestDto;
import ru.sparural.engine.api.dto.user.LoginViaOneTimeCodeUserDataDto;
import ru.sparural.engine.api.dto.user.LoginViaOneTimeCodeUserDataRequest;
import ru.sparural.gobals.Constants;
import ru.sparural.rest.services.AuthorizationService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.JwtResponse;
import ru.sparural.rest.security.TokenManager;

@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/${rest.version}/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Slf4j
@Api(tags = "login")
public class LoginController {

    private final AuthorizationService authorizationHelper;
    private final TokenManager tokenManager;

    @PostMapping
    @ApiOperation("login with phone number")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse login(@Valid @Parameter @RequestBody LoginUserDataRequestDto loginRequest,
                                   @Parameter(description = "web/mobile") @RequestHeader(value = Constants.CLIENT_TYPE_HEADER_NAME,
                                           defaultValue = Constants.CLIENT_TYPE_MOBILE) String client,
                                   @ApiIgnore HttpServletResponse response) {
        var token = authorizationHelper.loginByPhoneAndPassword(loginRequest, client, response, "login");
        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }
        return token;
    }

    @PostMapping("/two-fa")
    @ApiOperation("Exchange for one time code")
    @ResponseType(ControllerResponseType.WRAPPED)
    public CodeLoginResponse exchangeForOneTimeCode(@Valid @Parameter @RequestBody LoginViaOneTimeCodeUserDataRequest loginRequest) {
        return authorizationHelper.exchangeForOneTimeCode(loginRequest.getPhoneNumber());
    }

    @PostMapping("/two-fa-confirm")
    @ApiOperation("Login via one time code")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse loginViaOneTimeCode(@Valid @Parameter @RequestBody LoginViaOneTimeCodeUserDataDto loginViaOneTimeCodeUserDataDto,
                                   @Parameter(description = "web/mobile") @RequestHeader(value = Constants.CLIENT_TYPE_HEADER_NAME,
                                           defaultValue = Constants.CLIENT_TYPE_MOBILE) String client,
                                   @ApiIgnore HttpServletResponse response) {

        var token = authorizationHelper.loginViaOneTimeCode(loginViaOneTimeCodeUserDataDto);
        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }
        return token;
    }
}