package ru.sparural.notification.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Vyacheslav Vorobev
 * Создает и верифицирует JWT токен используя библеотеку <a href="https://github.com/auth0/java-jwt>auth0 java-jwt</a>
 * Параметры токена конфигурируются в application.yaml
 * {@link TokenManager#secret}       -- ключ подписи
 */
@Component
@RequiredArgsConstructor
@Getter
public class TokenManager {

    @Value("${security.jwt.secret-key}")
    private String secret;

    /**
     * Верификация и парсинг токена
     *
     * @return {@link DecodedJWT}
     * @see JWT
     */
    public DecodedJWT decodeAndVerifyRawToken(String jwtToken) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(jwtToken);
    }

}