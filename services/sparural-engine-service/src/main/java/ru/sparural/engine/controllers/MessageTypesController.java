package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.sparural.engine.api.dto.templates.MessageTypeDto;
import ru.sparural.engine.entity.MessageType;
import ru.sparural.engine.services.MessageTypeService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class MessageTypesController {
    private final MessageTypeService messageTypeService;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("message-types/index")
    public List<MessageTypeDto> msgType() {
        List<MessageType> entities = messageTypeService.findAll();
        return entities.stream().map(entity -> modelMapper.map(entity, MessageTypeDto.class)).collect(Collectors.toList());
    }
}
