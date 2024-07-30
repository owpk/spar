package ru.sparural.engine.api.dto.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingDto {

    @Min(value = -12, message = "The minimum value of the time zone is -12")
    @Max(value = 12, message = "The maximum value of the time zone is 12")
    Integer timezone;

    @Min(value = 0, message = "The minimum value of the notification frequency is 0")
    Integer notificationsFrequency;
}
