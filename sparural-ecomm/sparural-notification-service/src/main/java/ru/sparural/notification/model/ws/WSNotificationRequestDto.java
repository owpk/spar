package ru.sparural.notification.model.ws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.api.dto.ws.File;
import ru.sparural.notification.model.NotificationIdAware;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WSNotificationRequestDto implements NotificationIdAware {
    Long notificationId;
    String name;
    String message;
    String click_action;
    String merchantId;
    @JsonProperty
    File photo;
}
