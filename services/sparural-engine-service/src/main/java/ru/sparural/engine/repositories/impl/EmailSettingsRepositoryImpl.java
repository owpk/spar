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
                .set(EmailSettings.EMAIL_SETTINGS.DEVINOLOGIN, data.getDevinoLogin())
                .set(EmailSettings.EMAIL_SETTINGS.DEVINOPASSWORD, data.getDevinoPassword())
                .set(EmailSettings.EMAIL_SETTINGS.FREQUENCY, data.getFrequency())
                .set(EmailSettings.EMAIL_SETTINGS.SENDEREMAIL, data.getSenderEmail())
                .set(EmailSettings.EMAIL_SETTINGS.SENDERNAME, data.getSenderName())
                .set(EmailSettings.EMAIL_SETTINGS.UPDATEDAT, (new Date()).getTime())
                .returning().fetchOneInto(EmailSetting.class);
    }

    private EmailSetting createEmpty() {
        var create = dslContext.insertInto(EmailSettings.EMAIL_SETTINGS)
                .set(EmailSettings.EMAIL_SETTINGS.SENDERNAME, "")
                .set(EmailSettings.EMAIL_SETTINGS.SENDEREMAIL, "")
                .set(EmailSettings.EMAIL_SETTINGS.DEVINOLOGIN, "")
                .set(EmailSettings.EMAIL_SETTINGS.DEVINOPASSWORD, "")
                .set(EmailSettings.EMAIL_SETTINGS.CREATEDAT, new Date().getTime())
                .set(EmailSettings.EMAIL_SETTINGS.FREQUENCY, 0)
                .set(EmailSettings.EMAIL_SETTINGS.CREATEDAT, TimeHelper.currentTime())
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(EmailSetting.class);
    }
}
