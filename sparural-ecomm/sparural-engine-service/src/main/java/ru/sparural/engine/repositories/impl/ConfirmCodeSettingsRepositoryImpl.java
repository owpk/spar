package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ConfirmCodeSetting;
import ru.sparural.engine.repositories.ConfirmCodeSettingsRepository;
import ru.sparural.tables.ConfirmCodeSettings;

@Service
@RequiredArgsConstructor
public class ConfirmCodeSettingsRepositoryImpl implements ConfirmCodeSettingsRepository {
    private final DSLContext dslContext;

    @Override
    public ConfirmCodeSetting get() {
        var settings = dslContext.selectFrom(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(ConfirmCodeSetting.class);
    }

    @Override
    public ConfirmCodeSetting update(ConfirmCodeSetting data) {
        var updating = dslContext.update(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS)
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.LIFETIME, data.getLifetime())
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.MAX_DAYLY_COUNT, data.getMaxDaylyCount())
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.MAX_IN_HOUR_COUNT, data.getMaxInHourCount())
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.MAX_UNSUCCESSFUL_ATTEMPTS, data.getMaxUnsuccessfulAttempts())
                .returning().fetchOne();
        if (updating == null)
            return null;

        return updating.into(ConfirmCodeSetting.class);
    }

    private ConfirmCodeSetting createEmpty() {
        var create = dslContext.insertInto(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS)
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.LIFETIME, 0)
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.MAX_DAYLY_COUNT, 0)
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.MAX_IN_HOUR_COUNT, 0)
                .set(ConfirmCodeSettings.CONFIRM_CODE_SETTINGS.MAX_UNSUCCESSFUL_ATTEMPTS, 0)
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(ConfirmCodeSetting.class);
    }
}
