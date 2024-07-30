package ru.sparural.engine.loymax.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenExchangeResponse {
    Long id;
    @JsonProperty(value = "token_type")
    String tokenType;
    @JsonProperty(value = "access_token")
    String accessToken;
    @JsonProperty(value = "refresh_token")
    String refreshToken;
    @JsonProperty(value = "expires_in")
    Long expiresIn;

    public TokenExchangeResponse(String token) {
        this.accessToken = token;
    }
}