package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.DeliveryCreateDTO;
import ru.sparural.engine.api.dto.DeliveryDTO;
import ru.sparural.engine.api.dto.DeliveryUpdateDto;
import ru.sparural.engine.entity.DeliveryEntity;

import java.util.List;

public interface DeliveryService {

    List<DeliveryDTO> list(int offset, int limit, Boolean includeNotPublic);

    List<DeliveryDTO> list(int offset, int limit);

    DeliveryDTO get(Long id);

    Boolean delete(Long id);

    DeliveryDTO update(Long id, DeliveryUpdateDto deliveryDTO);

    DeliveryDTO create(DeliveryCreateDTO deliveryDTO);

    DeliveryEntity createEntityFromDTO(DeliveryCreateDTO deliveryDto);

    DeliveryEntity createEntityFromDTO(DeliveryUpdateDto deliveryDto);

    DeliveryDTO createDTOFromEntity(DeliveryEntity deliveryEntity);

    List<DeliveryDTO> createDTOListFromEntityList(List<DeliveryEntity> deliveryEntityList);

}
