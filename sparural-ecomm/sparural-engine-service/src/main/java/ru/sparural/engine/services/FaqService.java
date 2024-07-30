package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.FaqDTO;
import ru.sparural.engine.entity.FaqEntity;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

public interface FaqService {

    List<FaqDTO> list(int offset, int limit);

    FaqDTO get(Long id) throws ResourceNotFoundException;

    FaqDTO update(Long id, FaqDTO faqDTO);

    FaqEntity createEntityFromDTO(FaqDTO faqDTO);

    List<FaqDTO> createDTOListFromEntityList(List<FaqEntity> faqEntityList);

    FaqDTO createDTOFromEntity(FaqEntity faqEntity);

    Boolean delete(Long id);

    FaqDTO create(FaqDTO faqDTO);
}