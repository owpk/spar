package ru.sparural.engine.services;

import ru.sparural.engine.entity.UserDevice;

public interface UsersDeviceTypeService {

    UserDevice save(String deviceIdentifier, Long userId, UserDevice data);

    UserDevice getByUserId(Long deviceTypeId);

    String findByDeviceTypeId(Long deviceTypeId);
}
