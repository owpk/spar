package ru.sparural.engine.api.dto.templates;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TriggerRequestDto {
    Long triggerTypeId;
    Long dateStart;
    Long dateEnd;
    @NotNull(message = "Please specify frequency")
    Long frequency;
    @NotNull(message = "Please specify time of start")
    @Pattern(regexp = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$",
            message = "Wrong time format, should match HH:MM")
    String timeStart;
    @NotNull(message = "Please specify time of end")
    @Pattern(regexp = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$",
            message = "Wrong time format, should match HH:MM")
    String timeEnd;
}
