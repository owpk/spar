package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeEntity;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeFullEntity;
import ru.sparural.engine.repositories.MinVersionAppDeviceTypeRepository;
import ru.sparural.engine.services.MinVersionAppDeviceTypeService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinVersionAppDeviceTypeServiceImpl implements MinVersionAppDeviceTypeService {
    private final MinVersionAppDeviceTypeRepository minVersionAppDeviceTypeRepository;

    @Override
    public MinVersionAppDeviceTypeFullEntity getByDeviceTypeName(String deviceType) {
        return minVersionAppDeviceTypeRepository.getByDeviceTypeName(deviceType)
                .orElseThrow(() -> new ResourceNotFoundException("Not found by device type name: " + deviceType));
    }

    @Override
    public MinVersionAppDeviceTypeEntity create(MinVersionAppDeviceTypeEntity entity) {
        return minVersionAppDeviceTypeRepository.create(entity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot save entity: " + entity));
    }

    @Override
    public MinVersionAppDeviceTypeEntity update(MinVersionAppDeviceTypeEntity entity, Long id) {
        return minVersionAppDeviceTypeRepository.update(entity, id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update record with id: " + id));
    }

    @Override
    public Boolean delete(Long id) {
        return minVersionAppDeviceTypeRepository.delete(id);
    }

    @Override
    public List<MinVersionAppDeviceTypeFullEntity> getAll() {
        return minVersionAppDeviceTypeRepository.getAll();
    }
}
