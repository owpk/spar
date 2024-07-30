package ru.sparural.engine.api.dto.support;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportChatMessageDto {
    Long id;
    String messageType;
    String text;
    FileDto file;
    Boolean isReceived;
    Boolean isRead;
    Boolean draft;
    Long createdAt;
    Long updatedAt;
}
