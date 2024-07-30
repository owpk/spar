package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.DeregistrationConfirm;

public interface UserDeleteService {
    void deregistration(DeregistrationConfirm deregistrationConfirm, Long userId);
}
