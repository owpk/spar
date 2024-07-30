package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Setting;

public interface SettingsRepository {
    Setting get();

    Setting update(Setting data);
}
