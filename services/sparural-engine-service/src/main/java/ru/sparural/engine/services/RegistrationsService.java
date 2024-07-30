package ru.sparural.engine.services;

import ru.sparural.engine.entity.Registrations;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RegistrationsService {
    Registrations createRegistration(Long userId, Integer step);

    Registrations getByUserId(Long userId);

    List<Registrations> getAll();

    List<Long> findUsersWithNotCompletedRegistrations(List<Long> definedUsers, Long group);
}
