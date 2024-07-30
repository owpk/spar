package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.ViberSetting;
import ru.sparural.engine.repositories.ViberSettingsRepository;
import ru.sparural.tables.ViberSettings;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ViberSettingsRepositoryImpl implements ViberSettingsRepository {

    private final DSLContext dslContext;

    @Override
    public ViberSetting get() {
        var settings = dslContext.selectFrom(ViberSettings.VIBER_SETTINGS).fetchOne();
        if (settings == null) {
            return createEmpty();
        }
        return settings.into(ViberSetting.class);
    }

    @Override
    public ViberSetting update(ViberSetting data) {
        var update = dslContext.update(ViberSettings.VIBER_SETTINGS)
                .set(ViberSettings.VIBER_SETTINGS.DEVINOLOGIN, data.getDevinoLogin())
                .set(ViberSettings.VIBER_SETTINGS.DEVINOPASSWORD, data.getDevinoPassword())
                .set(ViberSettings.VIBER_SETTINGS.UPDATEDAT, new Date().getTime())
                .set(ViberSettings.VIBER_SETTINGS.FREQUENCY, data.getFrequency())
                .set(ViberSettings.VIBER_SETTINGS.SENDERNAME, data.getSenderName())
                .returning().fetchOne();
        if (update == null)
            return null;
        return update.into(ViberSetting.class);
    }

    private ViberSetting createEmpty() {
        var create = dslContext.insertInto(ViberSettings.VIBER_SETTINGS)
                .set(ViberSettings.VIBER_SETTINGS.DEVINOLOGIN, "")
                .set(ViberSettings.VIBER_SETTINGS.DEVINOPASSWORD, "")
                .set(ViberSettings.VIBER_SETTINGS.CREATEDAT, new Date().getTime())
                .set(ViberSettings.VIBER_SETTINGS.SENDERNAME, "")
                .set(ViberSettings.VIBER_SETTINGS.FREQUENCY, 0)
                .returning().fetchOne();
        if (create == null)
            return null;
        return create.into(ViberSetting.class);
    }
}
