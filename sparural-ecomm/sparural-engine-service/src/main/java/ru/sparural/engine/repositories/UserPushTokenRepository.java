package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.PushTokenReq;
import ru.sparural.engine.entity.UserPushTokensEntity;
import ru.sparural.tables.pojos.UserPushTokens;

import java.util.List;
import java.util.Optional;

public interface UserPushTokenRepository {
    boolean addPushToken(PushTokenReq pushTokenReq, Long userId);

    Optional<UserPushTokens> getByToken(String token);

    void createUsersPushTokenRecord(String deviceType, UserPushTokens userPushTokens);

    void update(UserPushTokens userPushTokens);

    List<UserPushTokensEntity> getAllByUserId(Long userId);

    void saveOrUpdate(UserPushTokens userPushTokens);

    void deleteById(Long id);

    void deleteByToken(String token);

    Optional<UserPushTokens> findByUserIdAndToken(Long userId, String token);
}
