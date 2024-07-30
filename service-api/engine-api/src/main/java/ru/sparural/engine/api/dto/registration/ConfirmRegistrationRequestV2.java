package ru.sparural.engine.api.dto.registration;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmRegistrationRequestV2 {

    @NotNull
    @Pattern(regexp = "\\d{4}", message = "The code must be a number and contain 4 digits")
    String confirmCode;

    String tempToken;
}
