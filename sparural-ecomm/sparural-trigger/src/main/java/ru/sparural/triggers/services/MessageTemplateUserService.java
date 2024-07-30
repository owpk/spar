package ru.sparural.triggers.services;

import ru.sparural.triggerapi.dto.MessageTemplatesUserDto;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTemplateUserService {
    List<MessageTemplatesUserDto> findByMessageTemplateId(List<Long> userIds);
}
