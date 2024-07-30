package ru.sparural.notification.service.impl.huawei.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.model.push.PushNotification;
import java.util.Map;

/**
 * @author Egor Novikov
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuaweiNotificationDto {

    @JsonProperty("validate_only")
    Boolean validateOnly;
    HuaweiMessage message;

    public HuaweiNotificationDto(PushNotification pushNotification, Map<String, String> params) {
        this.validateOnly = false;
        this.message = new HuaweiMessage(pushNotification, params);
    }
}
