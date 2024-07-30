package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.WhatsAppSetting;
import ru.sparural.engine.repositories.WhatsAppSettingsRepository;
import ru.sparural.tables.WhatsappSettings;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class WhatsAppSettingsRepositoryImpl implements WhatsAppSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public WhatsAppSetting get() {
        var settings = dslContext.selectFrom(WhatsappSettings.WHATSAPP_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(WhatsAppSetting.class);
    }

    @Override
    public WhatsAppSetting update(WhatsAppSetting data) {
        var update = dslContext.update(WhatsappSettings.WHATSAPP_SETTINGS)
                .set(WhatsappSettings.WHATSAPP_SETTINGS.FREQUENCY, data.getFrequency())
                .set(WhatsappSettings.WHATSAPP_SETTINGS.SENDER_NAME, data.getSenderName())
                .set(WhatsappSettings.WHATSAPP_SETTINGS.DEVINO_LOGIN, data.getDevinoLogin())
                .set(WhatsappSettings.WHATSAPP_SETTINGS.DEVINO_PASSWORD, data.getDevinoPassword())
                .set(WhatsappSettings.WHATSAPP_SETTINGS.UPDATED_AT, new Date().getTime())
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(WhatsAppSetting.class);
    }

    private WhatsAppSetting createEmpty() {
        var create = dslContext.insertInto(WhatsappSettings.WHATSAPP_SETTINGS)
                .set(WhatsappSettings.WHATSAPP_SETTINGS.DEVINO_PASSWORD, "")
                .set(WhatsappSettings.WHATSAPP_SETTINGS.DEVINO_LOGIN, "")
                .set(WhatsappSettings.WHATSAPP_SETTINGS.CREATED_AT, new Date().getTime())
                .set(WhatsappSettings.WHATSAPP_SETTINGS.SENDER_NAME, "")
                .set(WhatsappSettings.WHATSAPP_SETTINGS.FREQUENCY, 0)
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(WhatsAppSetting.class);
    }
}
