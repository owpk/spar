package ru.sparural.notification.service.impl.streamtelecom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamTelekomSms {
    String phone;
    String text;
}
