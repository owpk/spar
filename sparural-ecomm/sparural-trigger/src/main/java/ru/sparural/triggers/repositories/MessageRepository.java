package ru.sparural.triggers.repositories;

import ru.sparural.enums.MessageStatuses;
import ru.sparural.triggers.entities.Message;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageRepository {
    void save(Message message);

    Long findSendedAtByMessageTemplateId(Long messageTemplateId, Long userId);

    void updateStatus(String uuid, MessageStatuses status);
}
