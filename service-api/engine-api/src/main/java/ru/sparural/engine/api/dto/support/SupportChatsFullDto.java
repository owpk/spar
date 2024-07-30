package ru.sparural.engine.api.dto.support;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.user.UserDto;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportChatsFullDto {
    Long id;
    SupportUserInfoDto user;
    SupportChatFullMessageDto lastMessage;
    Long unreadMessagesCount;
}
