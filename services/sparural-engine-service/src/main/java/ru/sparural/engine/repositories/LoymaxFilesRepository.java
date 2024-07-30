package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxFileEntity;

import java.util.Optional;

public interface LoymaxFilesRepository {
    Optional<LoymaxFileEntity> checkIfExist(String loymaxFileId);

    Optional<LoymaxFileEntity> save(LoymaxFileEntity loymaxFileEntity);
}

