package ru.sparural.rest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.sparural.engine.api.dto.user.RoleDto;
import ru.sparural.engine.api.dto.user.TokenDataDto;

/**
 * @author Vyacheslav Vorobev
 * Создает и верифицирует JWT токен используя библеотеку <a href="https://github.com/auth0/java-jwt>auth0 java-jwt</a>
 * Параметры токена конфигурируются в application.yaml
 * {@link TokenManager#jwt_prefix}   -- тип токена
 * {@link TokenManager#jwtExpiresIn}    -- время действия
 * {@link TokenManager#secret}       -- ключ подписи
 */
@Component
@RequiredArgsConstructor
@Getter
public class TokenManager {

    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.token-prefix}")
    private String jwt_prefix;

    // seconds
    @Value("${security.jwt.expiration-time}")
    private Integer jwtExpiresIn;

    // seconds
    @Value("${security.refresh.expiration-time}")
    private Integer refreshExpiresIn;

    @Value("${security.cookie.secured}")
    private Boolean securedCookie;

    /**
     * Генерация токена
     * {@link TokenManager#jwtExpiresIn} значение используется в часах
     * Токен подписывается с использование алгоритма HmacSHA256
     *
     * @see JWT
     */
    public String createToken(String[] claims, Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withArrayClaim("role", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiresIn * 1000))
                .withIssuer("sparural")
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * Верификация и парсинг токена
     *
     * @return {@link DecodedJWT}
     * @see JWT
     */
    public DecodedJWT decodeAndVerifyRawToken(String jwtToken) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(jwtToken.replace(jwt_prefix, ""));
    }

    public Cookie createSecuredCookie(String name, String value, Integer maxAge) {
        var cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(securedCookie);
        return cookie;
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public String createAccessToken(TokenDataDto tokenDataDto) {
        var rolesResp = tokenDataDto.getRoles();
        var userIdResp = tokenDataDto.getUserId();
        String[] roles = rolesResp.stream()
                .map(RoleDto::getCode)
                .collect(Collectors.toList())
                .toArray(String[]::new);
        return createToken(roles, userIdResp);
    }
}