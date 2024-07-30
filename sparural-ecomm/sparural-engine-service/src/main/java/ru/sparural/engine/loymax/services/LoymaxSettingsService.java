package ru.sparural.engine.loymax.services;

import ru.sparural.engine.entity.LoymaxSetting;

public interface LoymaxSettingsService {

    LoymaxSetting update(LoymaxSetting data);

    LoymaxSetting get();

    void updateMaxCount(Long maxCount);
}
