package ru.sparural.triggers.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.triggerapi.dto.TriggersTypeDTO;
import ru.sparural.triggers.entities.TriggersType;
import ru.sparural.triggers.exceptions.ResourceNotFoundException;
import ru.sparural.triggers.repositories.TriggersTypesRepository;
import ru.sparural.triggers.utils.DtoMapperUtils;

import java.util.List;


@Service
@AllArgsConstructor
public class TriggersTypeServiceImpl implements ru.sparural.triggers.services.TriggersTypeService {

    private final TriggersTypesRepository triggersTypesRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<TriggersTypeDTO> list(int offset, int limit) {
        return createDTOListFromEntityList(triggersTypesRepository.fetch(offset, limit));
    }

    @Override
    public List<TriggersTypeDTO> createDTOListFromEntityList(List<TriggersType> triggersTypes) {
        return dtoMapperUtils.convertList(TriggersTypeDTO.class, triggersTypes);
    }

    @Override
    public List<TriggersTypeDTO> getAll() {
        return createDTOListFromEntityList(triggersTypesRepository.fetchAll());
    }

    @Override
    public TriggersTypeDTO get(Long id) {
        return dtoMapperUtils.convert(triggersTypesRepository.get(id)
                        .orElseThrow(() -> new ResourceNotFoundException("This type of trigger not exist")),
                TriggersTypeDTO.class);
    }
}
