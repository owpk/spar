package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.UserDevice;
import ru.sparural.engine.repositories.UserDeviceRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.UsersDevices;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDeviceRepositoryImpl implements UserDeviceRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<UserDevice> insert(UserDevice userDevice) {
        return dslContext.insertInto(UsersDevices.USERS_DEVICES)
                .set(UsersDevices.USERS_DEVICES.IDENTIFIER, userDevice.getIdentifier())
                .set(UsersDevices.USERS_DEVICES.USERID, userDevice.getUserId())
                .set(UsersDevices.USERS_DEVICES.DATA, userDevice.getData())
                .set(UsersDevices.USERS_DEVICES.VERSION_APP, userDevice.getVersionApp())
                .set(UsersDevices.USERS_DEVICES.CREATEDAT, TimeHelper.currentTime())
                .onConflict(UsersDevices.USERS_DEVICES.IDENTIFIER)
                .doUpdate()
                .set(UsersDevices.USERS_DEVICES.USERID, userDevice.getUserId())
                .set(UsersDevices.USERS_DEVICES.DATA, userDevice.getData())
                .set(UsersDevices.USERS_DEVICES.VERSION_APP, userDevice.getVersionApp())
                .set(UsersDevices.USERS_DEVICES.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(UserDevice.class);
    }

    @Override
    public Optional<UserDevice> getByUserId(Long userId) {
        return dslContext.select()
                .from(UsersDevices.USERS_DEVICES)
                .where(UsersDevices.USERS_DEVICES.USERID.eq(userId))
                .fetchOptionalInto(UserDevice.class);
    }
}