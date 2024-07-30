package ru.sparural.engine.api.dto.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsSettingsDto implements NotificationFrequencySettings {

    @Size(max = 255, message = "Maximum length of Devino login 255 characters")
    @NotBlank(message = "Please specify Devino login")
    String gatewayLogin;
    @Size(max = 255, message = "Maximum length of Devino password 255 characters")
    @NotBlank(message = "Please specify Devino password")
    String gatewayPassword;
    @Size(max = 11, message = "The maximum length of the sender's name is 11 characters")
    @Pattern(regexp = "\\w+", message = "The sender's name can only contain latin characters")
    @NotBlank(message = "The sender's name must not be an empty string")
    String senderName;
    @Min(value = 0)
    Integer frequency;
}
