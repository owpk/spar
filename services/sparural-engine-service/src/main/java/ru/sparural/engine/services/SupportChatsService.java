package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.support.SupportChatCreateMessageDto;
import ru.sparural.engine.entity.SupportChatMessageEntity;
import ru.sparural.engine.entity.SupportChatsEntity;

import java.util.List;

public interface SupportChatsService {
    List<SupportChatsEntity> index(Integer offset, Integer limit);

    SupportChatsEntity get(Long id);

    List<SupportChatMessageEntity> indexMessages(Long chatId, Long timeStamp, Integer limit);

    Long countUnreadMessages(Long senderId, Long chatId);

    SupportChatMessageEntity editMessage(Long chatId, Long messageId, SupportChatCreateMessageDto data);

    SupportChatMessageEntity createMessage(Long senderId, Long chatId, SupportChatCreateMessageDto data);

    List<SupportChatMessageEntity> setMessagesRead(
            Long chatId, List<Long> messagesIds);
}
