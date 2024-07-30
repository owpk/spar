package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.DeregistrationConfirm;
import ru.sparural.engine.entity.Deregistration;
import ru.sparural.engine.repositories.UserDeleteRepository;
import ru.sparural.engine.repositories.UserRepository;
import ru.sparural.engine.services.UserDeleteService;

@Service
@AllArgsConstructor
public class UserDeleteServiceImpl implements UserDeleteService {

    private final UserDeleteRepository userDeleteRepository;
    private final UserRepository userRepository;

    @Override
    public void deregistration(DeregistrationConfirm deregistrationConfirm, Long userId) {
        Deregistration deregistration = new Deregistration();
        deregistration.setUserId(userId);
        deregistration.setMessage(deregistrationConfirm.getMessage());
        deregistration.setReason("Reject");
        userDeleteRepository.create(deregistration);
        userRepository.delete(userId);
    }
}
