package ru.sparural.file.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Vyacheslav Vorobev
 * Создает и верифицирует JWT токен используя библеотеку <a href="https://github.com/auth0/java-jwt>auth0 java-jwt</a>
 * Параметры токена конфигурируются в application.yaml
 * {@link TokenManager#jwt_prefix}   -- тип токена
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

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}