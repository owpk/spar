package ru.sparural.triggers.repositories;

import ru.sparural.triggers.entities.MessageTemplate;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTemplateRepository {
    List<MessageTemplate> list(Integer offset, Integer limit, String messageType);

    Optional<MessageTemplate> get(Long id);

    Optional<MessageTemplate> create(MessageTemplate entity);

    Optional<MessageTemplate> update(Long id, MessageTemplate entity);

    Boolean delete(Long id);

    Optional<MessageTemplate> getByUserId(long userId);
}
