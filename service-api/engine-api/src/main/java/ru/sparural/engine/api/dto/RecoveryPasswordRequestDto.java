package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
public class RecoveryPasswordRequestDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "Please indicate the method of delivery of the code")
    @Pattern(regexp = "phoneNumber|email", message = "Possible phoneNumber and email values")
    String notifier;

    @NotNull(message = "Please enter your contact")
    @Size(max = 100, message = "Maximum number of characters 100")
    @Pattern(regexp = "(^7\\d{10}$|[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,4})",
            message = "Email or phone number is incorrect")
    String notifierIdentity;


    public RecoveryPasswordRequestDto(String notifierIdentity) {
        this.notifierIdentity = notifierIdentity;
    }
}
