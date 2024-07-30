package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.MerchantAttributeCreateOrUpdateDto;
import ru.sparural.engine.api.dto.merchant.Attribute;
import ru.sparural.engine.entity.AttributeEntity;
import ru.sparural.engine.repositories.AttributeRepository;
import ru.sparural.engine.services.AttributeService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository repository;
    private final DtoMapperUtils dtoMapperUtils;


    @Override
    public List<Attribute> list(Integer offset, Integer limit) {
        return createDtoList(repository.list(offset, limit));
    }

    @Override
    public Attribute get(Long id) {
        return createDto(repository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Attribute create(MerchantAttributeCreateOrUpdateDto attribute) {
        return createDto(repository.create(createEntity(attribute))
                .orElseThrow(() -> new ServiceException("Failed to create goods")));
    }

    @Override
    public Attribute update(Long id, MerchantAttributeCreateOrUpdateDto attribute) {
        if (attribute.getDraft() && !repository.findDraftById(id)) {
            throw new ValidationException("You can not change the value with False on True");
        }
        return createDto(repository.update(id, createEntity(attribute))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Boolean delete(Long id) {
        repository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return repository.delete(id);
    }

    @Override
    public Attribute createDto(AttributeEntity entity) {
        return dtoMapperUtils.convert(Attribute.class, () -> entity);
    }

    @Override
    public List<Attribute> createDtoList(List<AttributeEntity> entities) {
        return dtoMapperUtils.convertList(Attribute.class, () -> entities);
    }

    @Override
    public AttributeEntity createEntity(MerchantAttributeCreateOrUpdateDto dto) {
        return dtoMapperUtils.convert(AttributeEntity.class, () -> dto);
    }
}
