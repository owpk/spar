package ru.sparural.engine.api.dto.user;

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
public class ChangePasswordRequestDto {

    @NotNull(message = "Enter old password value")
    String oldPassword;

    @NotNull(message = "Enter new password value")
    @Password
    String newPassword;

}