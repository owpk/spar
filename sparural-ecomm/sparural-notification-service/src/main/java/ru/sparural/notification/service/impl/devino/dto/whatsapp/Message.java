package ru.sparural.notification.service.impl.devino.dto.whatsapp;

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
public class Message {
    String subject;
    String priority;
    Integer validityPeriodSec;
    String comment;
    String type;
    String contentType;
    Content content;
    String address;
    String smsText;
    String smsSrcAddress;
    String smsValidityPeriodSec;
}
