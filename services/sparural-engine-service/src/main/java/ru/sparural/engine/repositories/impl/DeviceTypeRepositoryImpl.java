package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.DeviceTypeRepository;
import ru.sparural.tables.DeviceTypes;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceTypeRepositoryImpl implements DeviceTypeRepository {
    private final DSLContext dslContext;
    private final DeviceTypes table = DeviceTypes.DEVICE_TYPES;

    @Override
    public Optional<String> findNameTypesById(Long id) {
        return dslContext.select(table.NAME).from(table)
                .where(table.ID.eq(id)).fetchOptionalInto(String.class);
    }
}
