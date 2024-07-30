package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Setting;
import ru.sparural.engine.repositories.SettingsRepository;
import ru.sparural.engine.services.SettingsService;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService<Setting> {

    private final SettingsRepository settingsRepository;

    @Override
    public Setting update(Setting data) {
        return settingsRepository.update(data);
    }

    @Override
    public Setting get() {
        return settingsRepository.get();
    }
}
