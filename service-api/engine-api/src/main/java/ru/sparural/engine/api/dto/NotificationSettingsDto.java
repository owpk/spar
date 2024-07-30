package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationSettingsDto {
    Boolean smsAllowed;
    Boolean emailAllowed;
    Boolean viberAllowed;
    Boolean whatsappAllowed;
    Boolean pushAllowed;
}
