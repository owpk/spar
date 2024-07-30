package ru.sparural.engine.api.dto.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PushSettingDto implements NotificationFrequencySettings {
    @Min(value = 0)
    Integer frequency;
    @Size(max = 255, message = "The maximum length of the id of project is 255 characters")
    @NotBlank(message = "The sender's name must not be an empty string")
    String firebaseProjectId;
    @Size(max = 255, message = "The maximum length of the id of Huawei app is 255 characters")
    @NotBlank(message = "The sender's name must not be an empty string")
    String huaweiAppId;
    @Size(max = 255, message = "The maximum length of the secret of Huawei app is 255 characters")
    @NotBlank(message = "The sender's name must not be an empty string")
    String huaweiAppSecret;
}
