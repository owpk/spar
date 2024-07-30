package ru.sparural.engine.api.dto;

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
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CodeDto {

    @NotNull(message = "Specify code value")
    @Pattern(regexp = "\\d{4}", message = "Code should contains 4 digits only")
    String code;
}
