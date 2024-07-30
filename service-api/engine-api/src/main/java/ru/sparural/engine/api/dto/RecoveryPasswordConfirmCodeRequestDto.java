package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Password;

import javax.validation.constraints.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecoveryPasswordConfirmCodeRequestDto {

    @NotNull(message = "Please enter your recovery token")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            message = "The recovery token must be in uuid format")
    String recoveryToken;

    @NotNull(message = "Please enter a confirmation code")
    @Digits(message = "", integer = 4, fraction = 0)
    @Min(value = 999L, message = "Number of characters in code 4")
    @Max(value = 9999L, message = "Number of characters in code 4")
    Integer code;

    @Password
    @NotNull(message = "Please enter a password")
    String password;
}
