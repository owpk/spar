package ru.sparural.notification.service.impl.firebase.dto.android;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.service.impl.firebase.dto.Action;
import ru.sparural.notification.service.impl.firebase.dto.Notification;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AndroidNotificationRequest {
    String name;
    Notification notification;
    Action android;
    String token;
}
