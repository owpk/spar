package ru.sparural.engine.loymax.rest.dto.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResult {
    String token_type;
    String access_token;
    String refresh_token;
    Long expires_in;
}
