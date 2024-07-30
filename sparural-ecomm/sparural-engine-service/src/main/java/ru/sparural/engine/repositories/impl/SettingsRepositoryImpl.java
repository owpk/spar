package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Setting;
import ru.sparural.engine.repositories.SettingsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Settings;

@Service
@RequiredArgsConstructor
public class SettingsRepositoryImpl implements SettingsRepository {

    private final DSLContext dslContext;

    @Override
    public Setting get() {
        var settings = dslContext.selectFrom(Settings.SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(Setting.class);
    }

    @Override
    public Setting update(Setting data) {
        var update = dslContext.update(Settings.SETTINGS)
                .set(Settings.SETTINGS.TIMEZONE, data.getTimezone())
                .set(Settings.SETTINGS.NOTIFICATIONS_FREQUENCY, data.getNotificationsFrequency())
                .set(Settings.SETTINGS.UPDATED_AT, TimeHelper.currentTime())
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(Setting.class);
    }

    private Setting createEmpty() {
        var create = dslContext.insertInto(Settings.SETTINGS)
                .set(Settings.SETTINGS.TIMEZONE, 0)
                .set(Settings.SETTINGS.NOTIFICATIONS_FREQUENCY, 0)
                .set(Settings.SETTINGS.CREATED_AT, TimeHelper.currentTime())
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(Setting.class);
    }
}
