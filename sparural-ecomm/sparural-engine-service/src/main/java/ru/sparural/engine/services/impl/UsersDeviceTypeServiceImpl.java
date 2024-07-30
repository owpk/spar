package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserDevice;
import ru.sparural.engine.repositories.DeviceTypeRepository;
import ru.sparural.engine.repositories.UserDeviceRepository;
import ru.sparural.engine.services.UsersDeviceTypeService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.lang.module.ResolutionException;

@Service
@RequiredArgsConstructor
public class UsersDeviceTypeServiceImpl implements UsersDeviceTypeService {

    private final UserDeviceRepository userDeviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;

    @Override
    public UserDevice save(String deviceIdentifier, Long userId, UserDevice data) {
        data.setIdentifier(deviceIdentifier);
        data.setUserId(userId);
        return userDeviceRepository.insert(data)
                .orElseThrow(ResolutionException::new);
    }

    @Override
    public UserDevice getByUserId(Long userId) {
        return userDeviceRepository.getByUserId(userId)
                .orElseThrow(ResolutionException::new);
    }

    @Override
    public String findByDeviceTypeId(Long deviceTypeId) {
        return deviceTypeRepository.findNameTypesById(deviceTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Device with this type not found"));
    }


}
