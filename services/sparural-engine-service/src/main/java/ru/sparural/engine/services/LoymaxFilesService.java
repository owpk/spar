package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.LoymaxFileDto;
import ru.sparural.engine.entity.LoymaxFileEntity;

public interface LoymaxFilesService {
    boolean IfFileExist(String loymaxFileId);

    void save(LoymaxFileDto loymaxFileDto);

    LoymaxFileEntity createEntityFromDTO(LoymaxFileDto loymaxFileDto);

    LoymaxFileDto createDTOFromEntity(LoymaxFileEntity loymaxFileEntity);
}

