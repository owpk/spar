package ru.sparural.notification.service.impl.devino.dto.viber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.notification.service.impl.devino.dto.whatsapp.Message;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevinoViberPushDto {
    Boolean resendSms;
    List<Message> messages;
}
