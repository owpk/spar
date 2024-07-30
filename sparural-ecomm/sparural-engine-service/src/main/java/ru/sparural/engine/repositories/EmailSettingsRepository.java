package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.EmailSetting;

public interface EmailSettingsRepository {
    EmailSetting get();

    EmailSetting update(EmailSetting data);
}
