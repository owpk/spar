package ru.sparural.engine.api.dto.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileUpdateRequest {

    @NotNull(message = "Пожалуйста укажите имя")
    @Size(min = 2, max = 100, message = "Минимальное количество символов в имени 2, максимальное 100")
    String firstName;

    @Size(max = 100, message = "Максимальное количество символов в фамилии 100")
    String lastName;

    @Pattern(regexp = "(male|female|other)", message = "Wrong gender, allowed (male|female|other)")
    @NotNull(message = "Пожалуйста укажите гендер")
    String gender;


    Long birthday;

    @Email(message = "Неправильный электронный адрес")
    @Size(max = 100, message = "Максимальное количестов символов в электронной почте 100")
    String email;
}
