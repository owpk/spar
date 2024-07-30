package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.SmsSetting;

public interface SmsSettingsRepository {

    SmsSetting get();

    SmsSetting update(SmsSetting data);
}
