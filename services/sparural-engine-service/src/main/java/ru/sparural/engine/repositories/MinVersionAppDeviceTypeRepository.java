package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.MinVersionAppDeviceTypeEntity;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeFullEntity;

import java.util.List;
import java.util.Optional;

public interface MinVersionAppDeviceTypeRepository {
    Optional<MinVersionAppDeviceTypeFullEntity> getByDeviceTypeName(String deviceType);

    Optional<MinVersionAppDeviceTypeEntity> create(MinVersionAppDeviceTypeEntity entity);

    Optional<MinVersionAppDeviceTypeEntity> update(MinVersionAppDeviceTypeEntity entity, Long id);

    Boolean delete(Long id);

    List<MinVersionAppDeviceTypeFullEntity> getAll();
}
