package ru.sparural.notification.service.impl.huawei.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Egor Novikov
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuaweiAndroidNotification {

    String title;
    String body;
    @JsonProperty("click_action")
    HuaweiAndroidClickAction clickAction;

    public HuaweiAndroidNotification(String title, String body) {
        this.title = title;
        this.body = body;
        this.clickAction = new HuaweiAndroidClickAction(3);
    }
}
