package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.WhatsAppSetting;

public interface WhatsAppSettingsRepository {

    WhatsAppSetting get();

    WhatsAppSetting update(WhatsAppSetting data);
}
