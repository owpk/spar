package ru.sparural.engine.api.dto.cards;

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
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardNumberRequestDto {

    @NotNull(message = "Please specify number")
    @Size(max = 19, message = "The number of characters in the card number must be 19")
    @Pattern(regexp = "\\d+")
    String number;
}