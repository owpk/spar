package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.gobals.Constants;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataResponse;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/${rest.version}/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Slf4j
@Api(tags = "logout")
public class LogoutController {

    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @PostMapping
    public DataResponse<?> logout(@ApiIgnore HttpServletResponse response,
                                  @ApiIgnore HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constants.ACCESS_TOKEN)
                        || cookie.getName().equals(Constants.REFRESH_TOKEN)) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        return DataResponse.builder().success(true).build();
    }
}