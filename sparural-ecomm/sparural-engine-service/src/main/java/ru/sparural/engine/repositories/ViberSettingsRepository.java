package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.ViberSetting;

public interface ViberSettingsRepository {

    ViberSetting get();

    ViberSetting update(ViberSetting data);

}
