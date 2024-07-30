package ru.sparural.notification.service.impl.huawei.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.model.push.PushNotification;

import java.util.List;
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
public class HuaweiMessage {

    HuaweiAndroidConfig android;
    List<String> token;

    public HuaweiMessage(PushNotification pushNotification, Map<String, String> params) {
        this.android = new HuaweiAndroidConfig(pushNotification, params);
        this.token = List.of(pushNotification.getPushToken());
    }
}