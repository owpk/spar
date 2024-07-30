package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ConfirmCodeSetting;
import ru.sparural.engine.repositories.ConfirmCodeSettingsRepository;
import ru.sparural.engine.services.ConfirmCodeSettingsService;

@Service
@AllArgsConstructor
public class ConfirmCodeSettingsServiceImpl implements ConfirmCodeSettingsService<ConfirmCodeSetting> {

    private final ConfirmCodeSettingsRepository confirmCodeSettingsRepository;

    @Override
    public ConfirmCodeSetting update(ConfirmCodeSetting data) {
        return confirmCodeSettingsRepository.update(data);
    }

    @Override
    public ConfirmCodeSetting get() {
        return confirmCodeSettingsRepository.get();
    }
}