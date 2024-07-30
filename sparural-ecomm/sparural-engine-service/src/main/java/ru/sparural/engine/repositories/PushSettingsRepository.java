package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.PushSetting;

public interface PushSettingsRepository {
    PushSetting get();

    PushSetting update(PushSetting data);
}
