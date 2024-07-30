package ru.sparural.engine.loymax.rest.dto.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteRegistrationResponse {
    Boolean registrationCompleted;
    String authToken;
    String token_type;
    String access_token;
    String refresh_token;
    Long expires_in;
}
