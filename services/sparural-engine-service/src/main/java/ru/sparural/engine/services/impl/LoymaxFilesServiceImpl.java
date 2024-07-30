package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.LoymaxFileDto;
import ru.sparural.engine.entity.LoymaxFileEntity;
import ru.sparural.engine.repositories.LoymaxFilesRepository;
import ru.sparural.engine.services.LoymaxFilesService;
import ru.sparural.engine.utils.DtoMapperUtils;

@Service
@RequiredArgsConstructor
public class LoymaxFilesServiceImpl implements LoymaxFilesService {
    private final LoymaxFilesRepository loymaxFilesRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public boolean IfFileExist(String loymaxFileId) {
        return loymaxFilesRepository.checkIfExist(loymaxFileId).isPresent();
    }

    @Override
    public void save(LoymaxFileDto loymaxFileDto) {
        loymaxFilesRepository.save(createEntityFromDTO(loymaxFileDto))
                .orElse(null);
    }

    @Override
    public LoymaxFileEntity createEntityFromDTO(LoymaxFileDto loymaxFileDto) {
        return dtoMapperUtils.convert(loymaxFileDto, LoymaxFileEntity.class);
    }

    @Override
    public LoymaxFileDto createDTOFromEntity(LoymaxFileEntity loymaxFileEntity) {
        return dtoMapperUtils.convert(loymaxFileEntity, LoymaxFileDto.class);
    }
}

