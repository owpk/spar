package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.FaqDTO;
import ru.sparural.engine.entity.FaqEntity;
import ru.sparural.engine.repositories.FaqRepository;
import ru.sparural.engine.services.FaqService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;


@Service
@AllArgsConstructor
public class FaqServiceImpl implements FaqService {

    private final DtoMapperUtils dtoMapperUtils;
    private final FaqRepository faqRepository;

    @Override
    public FaqEntity createEntityFromDTO(FaqDTO faqDTO) {
        return dtoMapperUtils.convert(faqDTO, FaqEntity.class);
    }

    @Override
    public FaqDTO createDTOFromEntity(FaqEntity faqEntity) {
        return dtoMapperUtils.convert(faqEntity, FaqDTO.class);
    }


    @Override
    public List<FaqDTO> createDTOListFromEntityList(List<FaqEntity> faqEntityList) {
        return dtoMapperUtils.convertList(FaqDTO.class, faqEntityList);
    }

    @Override
    public List<FaqDTO> list(int offset, int limit) {
        return createDTOListFromEntityList(faqRepository.fetch(offset, limit));
    }

    @Override
    public FaqDTO get(Long id) {
        return createDTOFromEntity(faqRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public FaqDTO update(Long id, FaqDTO faqDTO) {
        return createDTOFromEntity(faqRepository.update(id, createEntityFromDTO(faqDTO)).orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Boolean delete(Long id) {
        faqRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return faqRepository.delete(id);
    }

    @Override
    public FaqDTO create(FaqDTO faqDTO) {
        return createDTOFromEntity(faqRepository.create(createEntityFromDTO(faqDTO)).orElseThrow(() -> new ServiceException("Error creating faq")));
    }


}