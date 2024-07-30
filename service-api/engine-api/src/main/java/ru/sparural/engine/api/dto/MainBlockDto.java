package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.AllowNullButNotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainBlockDto {

    String code;

    @AllowNullButNotEmpty
    @Size(max = 255, message = "Maximum title length 255 characters")
    String name;

    @Min(value = 0, message = "Minimum value 0")
    @Max(value = 100, message = "Maximum value 100")
    Integer order;

    Boolean showCounter;
    Boolean showEndDate;
    Boolean showPercents;
    Boolean showBillet;
}
