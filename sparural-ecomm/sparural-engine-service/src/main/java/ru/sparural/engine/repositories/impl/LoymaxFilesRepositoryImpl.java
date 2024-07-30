package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxFileEntity;
import ru.sparural.engine.repositories.LoymaxFilesRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxFiles;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoymaxFilesRepositoryImpl implements LoymaxFilesRepository {
    private final DSLContext dslContext;
    LoymaxFiles table = LoymaxFiles.LOYMAX_FILES;

    @Override
    public Optional<LoymaxFileEntity> checkIfExist(String loymaxFileId) {
        return dslContext
                .selectFrom(table)
                .where(table.LOYMAX_FILE_ID.eq(loymaxFileId))
                .fetchOptionalInto(LoymaxFileEntity.class);
    }

    @Override
    public Optional<LoymaxFileEntity> save(LoymaxFileEntity loymaxFileEntity) {
        return dslContext
                .insertInto(table)
                .set(table.LOYMAX_FILE_ID, loymaxFileEntity.getLoymaxFileId())
                .set(table.FILE_UUID, loymaxFileEntity.getFileUuid())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(LoymaxFileEntity.class);
    }
}

