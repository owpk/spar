package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.DeliveryEntity;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    List<DeliveryEntity> fetch(int offset, int limit, Boolean includeNotPublic);

    List<DeliveryEntity> fetch(int offset, int limit);

    Optional<DeliveryEntity> get(Long id) throws ResourceNotFoundException;

    Boolean delete(Long id);

    Optional<DeliveryEntity> update(Long id, DeliveryEntity deliveryEntity) throws ResourceNotFoundException;

    Optional<DeliveryEntity> create(DeliveryEntity deliveryEntity);
}
