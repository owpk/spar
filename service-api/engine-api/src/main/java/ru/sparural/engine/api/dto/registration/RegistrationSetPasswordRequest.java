package ru.sparural.engine.api.dto.registration;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Password;

import javax.validation.constraints.NotNull;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationSetPasswordRequest {

    @Password
    @NotNull(message = "please enter a password")
    String password;
}