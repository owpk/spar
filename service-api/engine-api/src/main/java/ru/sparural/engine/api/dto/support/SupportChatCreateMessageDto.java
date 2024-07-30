package ru.sparural.engine.api.dto.support;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportChatCreateMessageDto {
    String messageType;
    String text;
    Boolean draft;
}
