package ru.sparural.triggers.repositories;

import ru.sparural.triggers.entities.MessageTemplateUsersGroup;

import java.util.List;
import java.util.Set;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTemplateUsersGroupRepository {
    List<MessageTemplateUsersGroup> list(Long messageTemplate);

    void save(Long userGroupId, Long messageTemplateId);

    void delete(Long messageTemplateId);

    void batchBind(Set<Long> usersGroup, Long id);
}
