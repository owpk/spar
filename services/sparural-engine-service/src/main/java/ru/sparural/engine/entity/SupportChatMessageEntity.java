package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SupportChatMessageEntity {
    private Long id;
    private Long chatId;
    private User sender;
    private String text;
    private Boolean isReceived;
    private Boolean isRead;
    private Boolean draft;
    private SupportChatMessageType messageType;
    private Long createdAt;
    private Long updatedAt;
}
