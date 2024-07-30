package ru.sparural.engine.services;

import ru.sparural.engine.entity.MinVersionAppDeviceTypeEntity;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeFullEntity;

import java.util.List;

public interface MinVersionAppDeviceTypeService {

    MinVersionAppDeviceTypeFullEntity getByDeviceTypeName(String deviceType);

    MinVersionAppDeviceTypeEntity create(MinVersionAppDeviceTypeEntity entity);

    MinVersionAppDeviceTypeEntity update(MinVersionAppDeviceTypeEntity entity, Long id);

    Boolean delete(Long id);

    List<MinVersionAppDeviceTypeFullEntity> getAll();

}
