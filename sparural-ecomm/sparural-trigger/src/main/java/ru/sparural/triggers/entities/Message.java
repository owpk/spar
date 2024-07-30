package ru.sparural.triggers.entities;

import lombok.Data;
import lombok.ToString;
import ru.sparural.enums.MessageStatuses;

@Data
@ToString
public class Message {
    String uuid;
    Long messageTemplateId;
    Long userId;
    MessageStatuses messageStatuses;
    Long sendedAt;
    Long triggerLogId;
    String data;
}
