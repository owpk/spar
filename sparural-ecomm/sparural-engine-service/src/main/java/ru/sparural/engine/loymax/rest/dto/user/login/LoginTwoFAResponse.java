package ru.sparural.engine.loymax.rest.dto.user.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginTwoFAResponse {
    String code;
    String message;
}
