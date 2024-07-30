package ru.sparural.notification.model.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.api.MessageTypes;
import ru.sparural.notification.api.dto.email.Recipient;
import ru.sparural.notification.api.dto.email.Sender;
import ru.sparural.notification.model.Notification;
import ru.sparural.notification.model.NotificationIdAware;
import ru.sparural.notification.model.push.Screen;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailNotification implements Notification {
    Long notificationId;
    Long userId;
    String messageUuid;
    Sender sender;
    List<Recipient> recipients;
    String subject;
    String message;
    Long lifetime;

    @Override
    public String getBody() {
        return String.format("subject: %s; message: %s; sender: %s", subject, message, sender);
    }

    @Override
    public String getTitle() {
        return subject;
    }

    @Override
    public Screen getScreen() {
        return null;
    }

    @Override
    public String getType() {
        return MessageTypes.EMAIL;
    }

    @Override
    public Long getMerchantId() {
        return null;
    }
}
