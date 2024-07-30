package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.AuthSetting;

public interface AuthSettingsRepository {
    AuthSetting get();

    AuthSetting update(AuthSetting data);
}
