package ru.sparural.notification.service.impl.firebase;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PushNotificationResponse {
    int status;
    String message;
}