package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserUnconfirmedEmail;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserUnconfirmedEmailRepository {
    void createRecord(UserUnconfirmedEmail userUnconfirmedEmail);

    Optional<UserUnconfirmedEmail> getByUserId(Long userId);

    void removeByEmail(String email);

    void removeByUser(Long userId);
}
