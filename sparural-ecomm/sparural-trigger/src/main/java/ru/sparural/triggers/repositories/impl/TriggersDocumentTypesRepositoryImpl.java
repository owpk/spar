package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.TriggersDocumentTypes;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TriggersDocumentTypesRepositoryImpl implements ru.sparural.triggers.repositories.TriggersDocumentTypesRepository {
    private final DSLContext dslContext;
    private final TriggersDocumentTypes table = TriggersDocumentTypes.TRIGGERS_DOCUMENT_TYPES;

    @Override
    public Optional<Long> findIdByName(String name) {
        return dslContext.select(table.ID)
                .from(table)
                .where(table.NAME.eq(name))
                .fetchOptionalInto(Long.class);
    }
}
