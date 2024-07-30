package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.EmailSetting;
import ru.sparural.engine.repositories.EmailSettingsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.EmailSettings;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class EmailSettingsRepositoryImpl implements EmailSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public EmailSetting get() {
        var settings = dslContext.selectFrom(EmailSettings.EMAIL_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(EmailSetting.class);
    }

    @Override
    public EmailSetting update(EmailSetting data) {
        return dslContext.update(EmailSettings.EMAIL_SETTINGS)
                .set(EmailSettings.EMAIL_SETTINGS.DEVINO_LOGIN, data.getDevinoLogin())
                .set(EmailSettings.EMAIL_SETTINGS.DEVINO_PASSWORD, data.getDevinoPassword())
                .set(EmailSettings.EMAIL_SETTINGS.FREQUENCY, data.getFrequency())
                .set(EmailSettings.EMAIL_SETTINGS.SENDER_EMAIL, data.getSenderEmail())
                .set(EmailSettings.EMAIL_SETTINGS.SENDER_NAME, data.getSenderName())
                .set(EmailSettings.EMAIL_SETTINGS.UPDATED_AT, (new Date()).getTime())
                .returning().fetchOneInto(EmailSetting.class);
    }

    private EmailSetting createEmpty() {
        var create = dslContext.insertInto(EmailSettings.EMAIL_SETTINGS)
                .set(EmailSettings.EMAIL_SETTINGS.SENDER_NAME, "")
                .set(EmailSettings.EMAIL_SETTINGS.SENDER_EMAIL, "")
                .set(EmailSettings.EMAIL_SETTINGS.DEVINO_LOGIN, "")
                .set(EmailSettings.EMAIL_SETTINGS.DEVINO_PASSWORD, "")
                .set(EmailSettings.EMAIL_SETTINGS.CREATED_AT, new Date().getTime())
                .set(EmailSettings.EMAIL_SETTINGS.FREQUENCY, 0)
                .set(EmailSettings.EMAIL_SETTINGS.CREATED_AT, TimeHelper.currentTime())
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(EmailSetting.class);
    }
}
