package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxSetting;
import ru.sparural.engine.repositories.LoymaxSettingsRepository;
import ru.sparural.tables.LoymaxSettings;

@Service
@RequiredArgsConstructor
public class LoymaxSettingsRepositoryImpl implements LoymaxSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public LoymaxSetting get() {
        var settings = dslContext.selectFrom(LoymaxSettings.LOYMAX_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(LoymaxSetting.class);
    }

    @Override
    public LoymaxSetting update(LoymaxSetting data) {
        var update = dslContext.update(LoymaxSettings.LOYMAX_SETTINGS)
                .set(LoymaxSettings.LOYMAX_SETTINGS.HOST, data.getHost())
                .set(LoymaxSettings.LOYMAX_SETTINGS.USERNAME, data.getUsername())
                .set(LoymaxSettings.LOYMAX_SETTINGS.PASSWORD, data.getPassword())
                .set(LoymaxSettings.LOYMAX_SETTINGS.MAX_FAFORITE_CATEGORIES_COUNT, data.getMaxFaforiteCategoriesCount())
                .set(LoymaxSettings.LOYMAX_SETTINGS.SITE_KEY, data.getSiteKey())
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(LoymaxSetting.class);
    }

    @Override
    public void updateMaxCount(Long maxCount) {
        var entity = dslContext
                .selectFrom(LoymaxSettings.LOYMAX_SETTINGS)
                .fetchOptional();
        if (entity.isEmpty()) {
            createEmpty();
        }
        dslContext.update(LoymaxSettings.LOYMAX_SETTINGS)
                .set(LoymaxSettings.LOYMAX_SETTINGS.MAX_FAFORITE_CATEGORIES_COUNT, maxCount)
                .returning().fetchOne();
    }


    private LoymaxSetting createEmpty() {
        var create = dslContext.insertInto(LoymaxSettings.LOYMAX_SETTINGS)
                .set(LoymaxSettings.LOYMAX_SETTINGS.HOST, "")
                .set(LoymaxSettings.LOYMAX_SETTINGS.USERNAME, "")
                .set(LoymaxSettings.LOYMAX_SETTINGS.PASSWORD, "")
                .set(LoymaxSettings.LOYMAX_SETTINGS.MAX_FAFORITE_CATEGORIES_COUNT, 0L)
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(LoymaxSetting.class);
    }

}
