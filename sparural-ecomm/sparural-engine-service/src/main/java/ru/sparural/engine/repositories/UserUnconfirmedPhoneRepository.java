package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserUnconfirmedPhone;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserUnconfirmedPhoneRepository {
    Optional<UserUnconfirmedPhone> saveOrUpdate(UserUnconfirmedPhone userUnconfirmedPhone);

    Optional<UserUnconfirmedPhone> getByUserId(Long id);

    boolean deleteByUserId(Long id);

    boolean removeByPhone(String phone);

    Optional<UserUnconfirmedPhone> getLastByUserId(Long userId);
}
