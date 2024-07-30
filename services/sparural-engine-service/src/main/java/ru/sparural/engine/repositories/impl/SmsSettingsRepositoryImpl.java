package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.SmsSetting;
import ru.sparural.engine.repositories.SmsSettingsRepository;
import ru.sparural.tables.SmsSettings;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SmsSettingsRepositoryImpl implements SmsSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public SmsSetting get() {
        var settings = dslContext.selectFrom(SmsSettings.SMS_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(SmsSetting.class);
    }

    @Override
    public SmsSetting update(SmsSetting data) {
        var update = dslContext.update(SmsSettings.SMS_SETTINGS)
                .set(SmsSettings.SMS_SETTINGS.FREQUENCY, data.getFrequency())
                .set(SmsSettings.SMS_SETTINGS.SENDERNAME, data.getSenderName())
                .set(SmsSettings.SMS_SETTINGS.GATEWAYLOGIN, data.getGatewayLogin())
                .set(SmsSettings.SMS_SETTINGS.GATEWAYPASSWORD, data.getGatewayPassword())
                .set(SmsSettings.SMS_SETTINGS.UPDATEDAT, new Date().getTime())
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(SmsSetting.class);
    }

    private SmsSetting createEmpty() {
        var create = dslContext.insertInto(SmsSettings.SMS_SETTINGS)
                .set(SmsSettings.SMS_SETTINGS.SENDERNAME, "")
                .set(SmsSettings.SMS_SETTINGS.GATEWAYLOGIN, "")
                .set(SmsSettings.SMS_SETTINGS.GATEWAYPASSWORD, "")
                .set(SmsSettings.SMS_SETTINGS.CREATEDAT, new Date().getTime())
                .set(SmsSettings.SMS_SETTINGS.FREQUENCY, 0)
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(SmsSetting.class);
    }

}
