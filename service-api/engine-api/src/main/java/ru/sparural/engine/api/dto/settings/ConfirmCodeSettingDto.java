package ru.sparural.engine.api.dto.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmCodeSettingDto {

    @Min(value = 0, message = "Lifetime minimum value 0")
    @Max(value = 10000, message = "Lifetime maximum value 10000")
    @NotNull(message = "Please indicate lifetime")
    Integer lifetime;

    @NotNull(message = "")
    @Min(value = 0, message = "Max unsuccessful attempts minimum value 0")
    @Max(value = 10000, message = "Max unsuccessful attempts maximum value 10000")
    Integer maxUnsuccessfulAttempts;

    @NotNull(message = "")
    @Min(value = 0, message = "In hour count minimum value 0")
    @Max(value = 10000, message = "In hour count maximum value 10000")
    Integer maxInHourCount;

    @NotNull(message = "")
    @Min(value = 0, message = "Max daily count minimum value 0")
    @Max(value = 10000, message = "Max daily count maximum value 10000")
    Integer maxDaylyCount;
}
