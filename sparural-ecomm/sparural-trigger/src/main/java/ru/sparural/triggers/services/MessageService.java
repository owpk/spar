package ru.sparural.triggers.services;

import ru.sparural.engine.api.dto.MessageDto;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageService {
    void save(MessageDto message);

    void updateStatus(String uuid, String status);

    Long findSendedAtByMessageTemplateId(Long messageTemplateId, Long userId);
}
