package ru.sparural.triggers.repositories;

import ru.sparural.triggers.entities.MessageTemplateUser;

import java.util.List;
import java.util.Set;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageTemplateUserRepository {
    List<MessageTemplateUser> findByMessageTemplateId(Long messageTemplateId);

    void save(Long userId, Long messageTemplateId);

    void delete(Long messageTemplateId);

    void batchBind(Set<Long> users, Long id);
}
