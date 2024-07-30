package ru.sparural.engine.api.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.PhoneNumberValidator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginUserDataRequestDto {

    @NotNull(message = "Пожалуйста укажите логин")
    @Pattern(regexp = "(" + PhoneNumberValidator.regexp + "|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4})",
            message = "Неверный логин")
    String phoneNumber;

    @NotNull(message = "Пожалуйста укажите пароль")
    String password;
}
