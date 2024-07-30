package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeregistrationConfirm {

    @Size(max = 1000, message = "Maximum number of characters in a message 1000")
    @NotBlank(message = "Please enter a message")
    String message;

    @Max(value = 4, message = "Number of digits must be 4")
    @Min(value = 4, message = "Number of digits must be 4")
    @NotNull(message = "Please enter a confirmation code")
    Integer code;
}
