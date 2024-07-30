package ru.sparural.engine.api.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginViaOneTimeCodeUserDataDto {

    @NotNull(message = "Temporary token must not be null")
    String tempToken;

    @NotNull(message = "Введите одноразовый код")
    @Pattern(regexp = "\\d{4}", message = "Одноразовый код должен содержать 4 цифры")
    String oneTimeCode;
}
