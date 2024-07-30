package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.PushTokenReqDto;
import ru.sparural.engine.entity.UserPushTokensEntity;
import ru.sparural.tables.pojos.UserPushTokens;

import java.util.List;

public interface UserPushTokenService {

    boolean savePushToken(PushTokenReqDto pushTokenReq, Long userId);

    UserPushTokens getByToken(String token);

    void bindAsyncPushTokenToUser(Long userId, PushTokenReqDto pushTokenReqDto);

    void updatePushTokenBind(UserPushTokens userPushTokens);

    List<UserPushTokensEntity> getAllByUserId(Long userId);

    void saveOrUpdate(UserPushTokens userPushTokens);

    void deleteById(Long id);

    void deleteByToken(String token);

    UserPushTokens findByUserIdAndToken(Long userId, String token);
}
