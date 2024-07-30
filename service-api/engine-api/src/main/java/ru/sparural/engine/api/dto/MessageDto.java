package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDto {
    String uuid;
    Long messageTemplateId;
    Long userId;
    String messageStatuses;
    String data;
    Long sendedAt;
    Long triggerLogId;
}
