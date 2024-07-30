package ru.sparural.triggers.services;

import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggerapi.dto.MessageTemplateRequestDto;
import ru.sparural.triggers.entities.MessageTemplate;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTemplateService {
    List<MessageTemplateDto> list(Integer offset, Integer limit, String messageType);

    MessageTemplateDto get(Long id);

    MessageTemplateDto create(MessageTemplateRequestDto messageTemplateDto);

    MessageTemplateDto update(Long id, MessageTemplateRequestDto messageTemplateDto);

    Boolean delete(Long id);

    List<MessageTemplateDto> createListDto(List<MessageTemplate> listEntity);

    MessageTemplate createEntity(MessageTemplateRequestDto dto);

    MessageTemplate getByUserId(long userId);

    MessageTemplateDto createDto(MessageTemplate entity);
}
