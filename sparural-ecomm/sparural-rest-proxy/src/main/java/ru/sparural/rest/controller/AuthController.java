package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.gobals.Constants;
import ru.sparural.rest.dto.TokenRefreshRequest;
import ru.sparural.rest.services.AuthorizationService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.JwtResponse;
import ru.sparural.rest.exception.RestRequestException;
import ru.sparural.rest.security.TokenManager;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/${rest.version}/auth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Slf4j
@Api(tags = "refresh token")
public class AuthController {

    private final AuthorizationService authorizationHelper;
    private final TokenManager tokenManager;

    @PostMapping("/refresh-tokens")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse refreshToken(@RequestHeader(value = Constants.CLIENT_TYPE_HEADER_NAME,
            defaultValue = Constants.CLIENT_TYPE_MOBILE) String client,
                                          @Parameter @RequestBody(required = false) TokenRefreshRequest tokenRefreshRequest,
                                          @CookieValue(value = Constants.REFRESH_TOKEN, required = false) String refreshTokenCookie,
                                          @ApiIgnore HttpServletResponse response) {
        String refreshToken = null;
        if (tokenRefreshRequest != null && !tokenRefreshRequest.getRefreshToken().isBlank()) {
            refreshToken = tokenRefreshRequest.getRefreshToken();
        } else if (refreshTokenCookie != null) {
            refreshToken = refreshTokenCookie;
        }

        if (refreshToken == null) {
            throw new RestRequestException("Request must contain refresh token value", 401);
        }

        var token = authorizationHelper.refreshTokenRequest(refreshToken);

        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }

        return token;
    }
}