package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.repositories.RegistrationsRepository;
import ru.sparural.engine.services.RegistrationsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RegistrationsServiceImpl implements RegistrationsService {
    private final RegistrationsRepository registrationsRepository;

    @Override
    public Registrations createRegistration(Long userId, Integer step) {
        var reg = new Registrations();
        reg.setUserId(userId);
        reg.setStep(step);
        var registrations = registrationsRepository.create(reg);
        return registrations.orElseThrow(() ->
                new RuntimeException("unexpected exception: cannot create registration for anonymous user"));
    }

    @Override
    public Registrations getByUserId(Long userId) {
        return registrationsRepository.getByUserId(userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<Registrations> getAll() {
        return registrationsRepository.getAll();
    }

    @Override
    public List<Long> findUsersWithNotCompletedRegistrations(List<Long> definedUsers, Long group) {
        return registrationsRepository.findUsersWithNotCompletedRegistrations(definedUsers, group)
                .stream().map(Registrations::getUserId).collect(Collectors.toList());
    }
}
