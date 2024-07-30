package ru.sparural.engine.services;

import ru.sparural.engine.entity.MessageType;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTypeService {

    List<MessageType> findAll();

}
