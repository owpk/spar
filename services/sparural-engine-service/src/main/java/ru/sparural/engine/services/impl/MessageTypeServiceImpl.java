package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MessageType;
import ru.sparural.engine.repositories.MessageTypeRepository;
import ru.sparural.engine.services.MessageTypeService;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class MessageTypeServiceImpl implements MessageTypeService {
    private final MessageTypeRepository messageTypeRepository;

    @Override
    public List<MessageType> findAll() {
        return messageTypeRepository.findAll();
    }
}
