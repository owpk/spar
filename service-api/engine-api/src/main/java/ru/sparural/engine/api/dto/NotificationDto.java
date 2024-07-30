package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.settings.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDto {

    @NotNull(message = "Please specify email settings")
    @Valid
    EmailSettingsDto email;

    @NotNull(message = "Please specify SMS settings")
    @Valid
    SmsSettingsDto sms;

    @NotNull(message = "Please enter your Viber settings")
    @Valid
    ViberSettingsDto viber;

    @NotNull(message = "Please enter your WhatsApp settings")
    @Valid
    WhatsappSettingsDto whatsapp;

    @NotNull(message = "Please enter your push settings")
    @Valid
    PushSettingDto push;
}
