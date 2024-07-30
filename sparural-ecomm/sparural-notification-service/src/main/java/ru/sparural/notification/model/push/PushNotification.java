package ru.sparural.notification.model.push;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.api.MessageTypes;
import ru.sparural.notification.api.dto.push.ScreenDto;
import ru.sparural.notification.model.Notification;
import ru.sparural.notification.model.NotificationIdAware;

/**
 * @author Vorobyev Vyacheslav
 */
@ToString
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PushNotification implements Notification {
    Long notificationId;
    Long userId;
    String title;
    String body;
    Boolean isReaded;
    String deviceType;
    String pushToken;
    String name;
    String message;
    Screen screen;
    Long merchantId;
    Long lifetime;

    @Override
    public String getType() {
        return MessageTypes.PUSH;
    }

}