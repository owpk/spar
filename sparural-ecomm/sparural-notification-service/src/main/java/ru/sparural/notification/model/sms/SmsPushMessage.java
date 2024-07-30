package ru.sparural.notification.model.sms;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.api.MessageTypes;
import ru.sparural.notification.api.dto.MessageDto;
import ru.sparural.notification.model.Notification;
import ru.sparural.notification.model.push.Screen;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsPushMessage implements Notification {
    Long notificationId;
    Long userId;
    List<MessageDto> messages;

    @Override
    public String getBody() {
        return messages.stream()
                .map(msg -> String.format("from: %s; to: %s; message: %s",
                        msg.getFrom(), msg.getTo(), msg.getText()))
                .collect(Collectors.joining());
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Screen getScreen() {
        return null;
    }

    @Override
    public String getType() {
        return MessageTypes.SMS;
    }

    @Override
    public Long getMerchantId() {
        return null;
    }
}
