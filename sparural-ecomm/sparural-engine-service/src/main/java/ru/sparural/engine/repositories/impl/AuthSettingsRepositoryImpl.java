package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AuthSetting;
import ru.sparural.engine.repositories.AuthSettingsRepository;
import ru.sparural.tables.AuthSettings;

@Service
@RequiredArgsConstructor
public class AuthSettingsRepositoryImpl implements AuthSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public AuthSetting get() {
        var settings = dslContext.selectFrom(AuthSettings.AUTH_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(AuthSetting.class);
    }

    @Override
    public AuthSetting update(AuthSetting data) {
        var updating = dslContext.update(AuthSettings.AUTH_SETTINGS)
                .set(AuthSettings.AUTH_SETTINGS.SECRET_KEY, data.getSecretKey())
                .returning().fetchOne();
        if (updating == null)
            return null;

        return updating.into(AuthSetting.class);
    }

    private AuthSetting createEmpty() {
        var create = dslContext.insertInto(AuthSettings.AUTH_SETTINGS)
                .set(AuthSettings.AUTH_SETTINGS.SECRET_KEY, "")
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(AuthSetting.class);
    }
}
