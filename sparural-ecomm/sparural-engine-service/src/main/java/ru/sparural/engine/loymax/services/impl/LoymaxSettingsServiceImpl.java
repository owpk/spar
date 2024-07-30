package ru.sparural.engine.loymax.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxSetting;
import ru.sparural.engine.loymax.services.LoymaxSettingsService;
import ru.sparural.engine.repositories.impl.LoymaxSettingsRepositoryImpl;


@Service
@RequiredArgsConstructor
public class LoymaxSettingsServiceImpl implements LoymaxSettingsService {

    private final LoymaxSettingsRepositoryImpl loymaxSettingsRepository;

    @Override
    public LoymaxSetting update(LoymaxSetting data) {
        return loymaxSettingsRepository.update(data);
    }

    @Override
    public LoymaxSetting get() {
        return loymaxSettingsRepository.get();
    }

    @Override
    public void updateMaxCount(Long maxCount) {
        loymaxSettingsRepository.updateMaxCount(maxCount);
    }
}