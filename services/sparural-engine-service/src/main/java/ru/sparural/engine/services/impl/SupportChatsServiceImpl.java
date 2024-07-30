package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.support.SupportChatCreateMessageDto;
import ru.sparural.engine.entity.SupportChatMessageEntity;
import ru.sparural.engine.entity.SupportChatsEntity;
import ru.sparural.engine.repositories.SupportChatsRepository;
import ru.sparural.engine.services.SupportChatsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportChatsServiceImpl implements SupportChatsService {
    private final SupportChatsRepository supportChatsRepository;

    @Override
    public List<SupportChatsEntity> index(Integer offset, Integer limit) {
        return supportChatsRepository.list(offset, limit);
    }

    @Override
    public SupportChatsEntity get(Long id) {
        return supportChatsRepository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.valueOf(id)));
    }

    @Override
    public List<SupportChatMessageEntity> indexMessages(Long chatId, Long timeStamp, Integer limit) {
        return supportChatsRepository.list(chatId, timeStamp, limit)
                .stream()
                .map(SupportChatsEntity::getMessage)
                .collect(Collectors.toList());
    }

    @Override
    public Long countUnreadMessages(Long senderId, Long chatId) {
        return supportChatsRepository.countUnreadMessages(senderId, chatId);
    }

    @Override
    public SupportChatMessageEntity editMessage(Long chatId, Long messageId, SupportChatCreateMessageDto data) {
        // TODO task 278 277
        return supportChatsRepository.updateMessage(chatId, messageId, data)
                .orElseThrow(() -> new RuntimeException("Cannot update message with id: " + messageId));
    }

    @Override
    public SupportChatMessageEntity createMessage(Long senderId, Long chatId, SupportChatCreateMessageDto data) {
        // TODO task 278 277
        return supportChatsRepository.createMessage(senderId, chatId, data)
                .orElseThrow(() -> new RuntimeException("Cannot create message for chat with id: " + chatId));
    }

    @Override
    public List<SupportChatMessageEntity> setMessagesRead(
            Long chatId, List<Long> messagesIds) {
        return supportChatsRepository.setMessagesRead(chatId, messagesIds);
    }
}
