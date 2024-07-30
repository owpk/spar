package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SupportChatsEntity {
    private Long id;
    private User user;
    private SupportChatMessageEntity message;
}
