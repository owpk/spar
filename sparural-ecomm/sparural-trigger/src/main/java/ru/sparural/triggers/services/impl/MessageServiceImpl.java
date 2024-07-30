package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.MessageDto;
import ru.sparural.enums.MessageStatuses;
import ru.sparural.triggers.entities.Message;
import ru.sparural.triggers.repositories.MessageRepository;
import ru.sparural.triggers.utils.DtoMapperUtils;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements ru.sparural.triggers.services.MessageService {
    private final MessageRepository messageRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public void save(MessageDto message) {
        var entity = dtoMapperUtils.convert(message, Message.class);
        entity.setMessageStatuses(MessageStatuses.BeingSent);
        messageRepository.save(entity);
    }

    @Override
    public void updateStatus(String uuid, String status) {
        messageRepository.updateStatus(uuid, MessageStatuses.valueOf(status));
    }

    @Override
    public Long findSendedAtByMessageTemplateId(Long messageTemplateId, Long userId) {
        return messageRepository.findSendedAtByMessageTemplateId(messageTemplateId, userId);
    }
}
