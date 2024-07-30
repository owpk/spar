package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxSetting;

public interface LoymaxSettingsRepository {

    LoymaxSetting get();

    LoymaxSetting update(LoymaxSetting data);

    void updateMaxCount(Long maxCount);
}
