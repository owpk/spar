package ru.sparural.notification.service.impl.huawei.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.model.push.PushNotification;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Egor Novikov
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuaweiAndroidConfig {

    HuaweiAndroidNotification notification;
    String data;

    public HuaweiAndroidConfig(PushNotification pushNotification, Map<String, String> params) {
        this.notification = new HuaweiAndroidNotification(pushNotification.getTitle(), pushNotification.getBody());
        this.data = new StringBuilder()
                .append("{")
                .append(params.entrySet().stream()
                        .map(x -> String.format("'%s':'%s'", x.getKey(), x.getValue()))
                        .collect(Collectors.joining(",")))
                .append("}").toString();
    }
}
