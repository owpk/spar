package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.UserDevice;

import java.util.Optional;

public interface UserDeviceRepository {
    Optional<UserDevice> insert(UserDevice userDevice);

    Optional<UserDevice> getByUserId(Long deviceTypeId);
}
