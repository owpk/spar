package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Registrations;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RegistrationsRepository {
    Optional<Registrations> create(Registrations data);

    Optional<Registrations> update(Registrations registrations);

    Optional<Registrations> getByUserId(Long userId);

    Optional<Registrations> getByPhoneNumber(String phoneNumber);

    List<Registrations> getAll();

    List<Registrations> findUsersWithNotCompletedRegistrations(List<Long> definedUsers, Long groupId);
}
