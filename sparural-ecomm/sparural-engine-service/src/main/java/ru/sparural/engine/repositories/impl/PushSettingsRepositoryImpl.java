package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.PushSetting;
import ru.sparural.engine.repositories.PushSettingsRepository;
import ru.sparural.tables.PushSettings;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PushSettingsRepositoryImpl implements PushSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public PushSetting get() {
        var settings = dslContext.selectFrom(PushSettings.PUSH_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(PushSetting.class);
    }

    @Override
    public PushSetting update(PushSetting data) {
        var create = dslContext.update(PushSettings.PUSH_SETTINGS)
                .set(PushSettings.PUSH_SETTINGS.HUAWEI_APP_ID, data.getHuaweiAppId())
                .set(PushSettings.PUSH_SETTINGS.HUAWEI_APP_SECRET, data.getHuaweiAppSecret())
                .set(PushSettings.PUSH_SETTINGS.FIREBASE_PROJECT_ID, data.getFirebaseProjectId())
                .set(PushSettings.PUSH_SETTINGS.FREQUENCY, data.getFrequency())
                .set(PushSettings.PUSH_SETTINGS.UPDATED_AT, new Date().getTime())
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(PushSetting.class);
    }

    private PushSetting createEmpty() {
        var create = dslContext.insertInto(PushSettings.PUSH_SETTINGS)
                .set(PushSettings.PUSH_SETTINGS.HUAWEI_APP_ID, "")
                .set(PushSettings.PUSH_SETTINGS.HUAWEI_APP_SECRET, "")
                .set(PushSettings.PUSH_SETTINGS.FIREBASE_PROJECT_ID, "")
                .set(PushSettings.PUSH_SETTINGS.FREQUENCY, 0)
                .set(PushSettings.PUSH_SETTINGS.CREATED_AT, new Date().getTime())
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(PushSetting.class);
    }
}
