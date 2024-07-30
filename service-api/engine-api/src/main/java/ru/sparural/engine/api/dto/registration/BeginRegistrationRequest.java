package ru.sparural.engine.api.dto.registration;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Phone;

import javax.validation.constraints.NotNull;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BeginRegistrationRequest {

    @Phone
    @NotNull(message = "Please enter your phone number")
    String phoneNumber;

}
