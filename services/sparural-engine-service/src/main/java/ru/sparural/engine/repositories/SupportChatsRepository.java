package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.support.SupportChatCreateMessageDto;
import ru.sparural.engine.entity.SupportChatMessageEntity;
import ru.sparural.engine.entity.SupportChatsEntity;

import java.util.List;
import java.util.Optional;

public interface SupportChatsRepository {
    Optional<SupportChatsEntity> fetchById(Long id);

    List<SupportChatsEntity> list(Integer offset, Integer limit);

    List<SupportChatsEntity> list(Long chatId, Long timeStamp, Integer limit);

    Long countUnreadMessages(Long senderId, Long chatId);

    Optional<SupportChatMessageEntity> updateMessage(Long chatId, Long messageId, SupportChatCreateMessageDto data);

    Optional<SupportChatMessageEntity> createMessage(Long senderId, Long chatId, SupportChatCreateMessageDto data);

    List<SupportChatMessageEntity> setMessagesRead(Long chatId, List<Long> messagesIds);
}
