package ru.sparural.engine.api.dto.support;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportChatReadMessagesInfoDto {
    List<Long> messagesIds;
}
