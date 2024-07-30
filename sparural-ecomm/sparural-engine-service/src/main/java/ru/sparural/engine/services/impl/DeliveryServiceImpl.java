package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.DeliveryCreateDTO;
import ru.sparural.engine.api.dto.DeliveryDTO;
import ru.sparural.engine.api.dto.DeliveryUpdateDto;
import ru.sparural.engine.entity.DeliveryEntity;
import ru.sparural.engine.repositories.DeliveryRepository;
import ru.sparural.engine.services.DeliveryService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;

    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<DeliveryDTO> list(int offset, int limit, Boolean includeNotPublic) {
        return createDTOListFromEntityList(deliveryRepository.fetch(offset, limit, includeNotPublic));
    }

    @Override
    public List<DeliveryDTO> list(int offset, int limit) {
        return createDTOListFromEntityList(deliveryRepository.fetch(offset, limit));
    }

    @Override
    public DeliveryDTO get(Long id) {
        return createDTOFromEntity(deliveryRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Boolean delete(Long id) {
        deliveryRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return deliveryRepository.delete(id);
    }


    @Override
    public DeliveryDTO update(Long id, DeliveryUpdateDto deliveryDTO) {
        return createDTOFromEntity(deliveryRepository.update(id, createEntityFromDTO(deliveryDTO))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public DeliveryDTO create(DeliveryCreateDTO deliveryDTO) {
        return createDTOFromEntity(deliveryRepository.create(createEntityFromDTO(deliveryDTO))
                .orElseThrow(() -> new ServiceException("Failed to create delivery")));
    }

    @Override
    public DeliveryEntity createEntityFromDTO(DeliveryCreateDTO deliveryDto) {
        return dtoMapperUtils.convert(deliveryDto, DeliveryEntity.class);
    }

    @Override
    public DeliveryEntity createEntityFromDTO(DeliveryUpdateDto deliveryDto) {
        return dtoMapperUtils.convert(deliveryDto, DeliveryEntity.class);
    }

    @Override
    public DeliveryDTO createDTOFromEntity(DeliveryEntity deliveryEntity) {
        return dtoMapperUtils.convert(deliveryEntity, DeliveryDTO.class);
    }


    @Override
    public List<DeliveryDTO> createDTOListFromEntityList(List<DeliveryEntity> deliveryEntityList) {
        return dtoMapperUtils.convertList(DeliveryDTO.class, deliveryEntityList);
    }

}
