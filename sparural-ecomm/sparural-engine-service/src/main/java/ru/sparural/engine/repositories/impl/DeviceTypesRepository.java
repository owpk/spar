package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.DeviceType;
import ru.sparural.tables.DeviceTypes;

@Service
@RequiredArgsConstructor
public class DeviceTypesRepository {

    private final DSLContext dslContext;

    public DeviceType getByName(String name) {
        var device = dslContext
                .selectFrom(DeviceTypes.DEVICE_TYPES)
                .where(DeviceTypes.DEVICE_TYPES.NAME.eq(name))
                .fetchOne();
        if (device == null) {
            return null;
        }
        return device.into(DeviceType.class);

    }

}
