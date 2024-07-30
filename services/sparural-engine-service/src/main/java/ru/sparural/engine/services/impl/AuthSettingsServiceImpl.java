package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AuthSetting;
import ru.sparural.engine.repositories.AuthSettingsRepository;
import ru.sparural.engine.services.AuthSettingsService;

@Service
@AllArgsConstructor
public class AuthSettingsServiceImpl implements AuthSettingsService<AuthSetting> {

    private final AuthSettingsRepository authSettingsRepository;

    @Override
    public AuthSetting update(AuthSetting data) {
        return authSettingsRepository.update(data);
    }

    @Override
    public AuthSetting get() {
        return authSettingsRepository.get();
    }
}
