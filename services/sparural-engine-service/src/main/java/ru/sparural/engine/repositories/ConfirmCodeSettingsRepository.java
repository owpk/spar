package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.ConfirmCodeSetting;

public interface ConfirmCodeSettingsRepository {

    ConfirmCodeSetting get();

    ConfirmCodeSetting update(ConfirmCodeSetting data);
}
