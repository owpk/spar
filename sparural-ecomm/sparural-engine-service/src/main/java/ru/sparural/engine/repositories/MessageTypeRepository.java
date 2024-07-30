package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MessageType;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTypeRepository {
    List<MessageType> findAll();
}
