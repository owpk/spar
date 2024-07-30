package ru.sparural.triggers.repositories;

import ru.sparural.triggers.entities.MessageType;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTypeRepository {
    Optional<MessageType> get(Long id);

    Optional<MessageType> findByName(String messageType);
}
