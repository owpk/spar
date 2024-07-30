package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.TriggersTypes;
import ru.sparural.triggers.entities.TriggersType;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TriggersTypesRepositoryImpl implements ru.sparural.triggers.repositories.TriggersTypesRepository {

    private final DSLContext dslContext;

    @Override
    public List<TriggersType> fetch(int offset, int limit) {
        return dslContext
                .selectFrom(TriggersTypes.TRIGGERS_TYPES)
                .offset(offset)
                .limit(limit)
                .fetch().into(TriggersType.class);
    }

    @Override
    public List<TriggersType> fetchAll() {
        return dslContext
                .selectFrom(TriggersTypes.TRIGGERS_TYPES)
                .fetch().into(TriggersType.class);
    }

    @Override
    public Optional<TriggersType> get(Long id) {
        return dslContext.selectFrom(TriggersTypes.TRIGGERS_TYPES)
                .where(TriggersTypes.TRIGGERS_TYPES.ID.eq(id))
                .fetchOptionalInto(TriggersType.class);
    }
}
