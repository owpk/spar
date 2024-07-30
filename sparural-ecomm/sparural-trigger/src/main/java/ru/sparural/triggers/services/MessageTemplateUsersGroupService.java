package ru.sparural.triggers.services;

import ru.sparural.triggerapi.dto.MessageTemplateUsersGroupDto;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTemplateUsersGroupService {
    List<MessageTemplateUsersGroupDto> findByMessageTemplateId(List<Long> userGroupIds);
}
