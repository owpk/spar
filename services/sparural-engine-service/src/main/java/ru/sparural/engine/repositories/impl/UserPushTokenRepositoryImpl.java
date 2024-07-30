package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.PushTokenReq;
import ru.sparural.engine.entity.UserPushTokensEntity;
import ru.sparural.engine.repositories.UserPushTokenRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.DeviceTypes;
import ru.sparural.tables.daos.UserPushTokensDao;
import ru.sparural.tables.pojos.UserPushTokens;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPushTokenRepositoryImpl implements UserPushTokenRepository {

    private final DSLContext dsl;
    private UserPushTokensDao dao;

    @PostConstruct
    public void init() {
        dao = new UserPushTokensDao(dsl.configuration());
    }

    @Transactional
    public boolean addPushToken(PushTokenReq pushTokenReq, Long userId) {
        return dsl.insertInto(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS)
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN, pushTokenReq.getToken())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.USERID, userId)
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.DEVICETYPEID,
                        dsl.select().from(DeviceTypes.DEVICE_TYPES)
                                .where(DeviceTypes.DEVICE_TYPES.NAME.eq(pushTokenReq.getDeviceType()))
                                .fetchOptionalInto(ru.sparural.tables.pojos.DeviceTypes.class)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Device type with name: " + pushTokenReq.getDeviceType() + " not found"))
                                .getId())
                .onConflictDoNothing()
                .execute() == 1;
    }

    @Override
    public Optional<UserPushTokens> getByToken(String token) {
        return dsl.select()
                .from(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS)
                .where(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN.eq(token))
                .fetchOptionalInto(UserPushTokens.class);
    }

    @Override
    @Transactional
    public void createUsersPushTokenRecord(String deviceType, UserPushTokens userPushTokens) {
        dsl.insertInto(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS)
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.USERID, userPushTokens.getUserid())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN, userPushTokens.getToken())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.CREATEDAT, TimeHelper.currentTime())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.DEVICETYPEID,
                        dsl.select(DeviceTypes.DEVICE_TYPES.ID).from(DeviceTypes.DEVICE_TYPES)
                                .where(DeviceTypes.DEVICE_TYPES.NAME.eq(deviceType))
                                .fetchOptionalInto(ru.sparural.tables.pojos.DeviceTypes.class)
                                .orElseThrow(() -> new ResourceNotFoundException("Device type " + "\"" + deviceType + "\"" + " not found"))
                                .getId())
                .onConflict(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN)
                .doUpdate()
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.USERID, userPushTokens.getUserid())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.CREATEDAT, TimeHelper.currentTime())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.DEVICETYPEID,
                        dsl.select(DeviceTypes.DEVICE_TYPES.ID).from(DeviceTypes.DEVICE_TYPES)
                                .where(DeviceTypes.DEVICE_TYPES.NAME.eq(deviceType))
                                .fetchOptionalInto(ru.sparural.tables.pojos.DeviceTypes.class)
                                .orElseThrow(() -> new ResourceNotFoundException("Device type " + "\"" + deviceType + "\"" + " not found"))
                                .getId())
                .executeAsync();
    }

    @Override
    public void update(UserPushTokens userPushTokens) {
        dsl.update(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS)
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN, userPushTokens.getToken())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.USERID, userPushTokens.getUserid())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.DEVICETYPEID, userPushTokens.getDevicetypeid())
                .set(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.UPDATEDAT, TimeHelper.currentTime())
                .execute();
    }

    @Override
    public List<UserPushTokensEntity> getAllByUserId(Long userId) {
        var userPushTokensTable = ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS;
        return dsl.select(
                        userPushTokensTable.ID,
                        userPushTokensTable.TOKEN,
                        userPushTokensTable.DEVICETYPEID,
                        userPushTokensTable.UPDATEDAT,
                        userPushTokensTable.CREATEDAT,
                        DeviceTypes.DEVICE_TYPES.NAME
                )
                .from(userPushTokensTable)
                .leftJoin(DeviceTypes.DEVICE_TYPES)
                .on(userPushTokensTable.DEVICETYPEID.eq(DeviceTypes.DEVICE_TYPES.ID))
                .where(userPushTokensTable.USERID.eq(userId))
                .fetch(record -> {
                    var entity = new UserPushTokensEntity();
                    entity.setId(record.get(userPushTokensTable.ID));
                    entity.setToken(record.get(userPushTokensTable.TOKEN));
                    entity.setDevicetypeid(record.get(userPushTokensTable.DEVICETYPEID));
                    entity.setDevicetype(record.get(DeviceTypes.DEVICE_TYPES.NAME));
                    entity.setUpdatedat(record.get(userPushTokensTable.UPDATEDAT));
                    entity.setCreatedat(record.get(userPushTokensTable.CREATEDAT));
                    return entity;
                });
    }

    @Override
    public void saveOrUpdate(UserPushTokens userPushTokens) {
        dao.merge(userPushTokens);
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    @Override
    public void deleteByToken(String token) {
        dsl.delete(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS)
                .where(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN.eq(token))
                .execute();
    }

    @Override
    public Optional<UserPushTokens> findByUserIdAndToken(Long userId, String token) {
        return dsl.select()
                .from(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS)
                .where(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.TOKEN.eq(token)
                        .and(ru.sparural.tables.UserPushTokens.USER_PUSH_TOKENS.USERID.eq(userId)))
                .fetchOptionalInto(UserPushTokens.class);
    }

}