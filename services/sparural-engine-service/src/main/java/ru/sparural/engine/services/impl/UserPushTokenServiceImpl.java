package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.PushTokenReqDto;
import ru.sparural.engine.entity.PushTokenReq;
import ru.sparural.engine.entity.UserPushTokensEntity;
import ru.sparural.engine.repositories.UserPushTokenRepository;
import ru.sparural.engine.services.UserPushTokenService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.tables.pojos.UserPushTokens;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPushTokenServiceImpl implements UserPushTokenService {

    private final UserPushTokenRepository userPushTokenRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public boolean savePushToken(PushTokenReqDto pushTokenReq, Long userId) {
        return userPushTokenRepository.addPushToken(dtoMapperUtils.convert(pushTokenReq, PushTokenReq.class), userId);
    }

    @Override
    public UserPushTokens getByToken(String token) {
        return userPushTokenRepository.getByToken(token)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public void bindAsyncPushTokenToUser(Long id, PushTokenReqDto pushTokenReqDto) {
        var userPushTokens = new UserPushTokens();
        userPushTokens.setToken(pushTokenReqDto.getToken());
        userPushTokens.setUserid(id);
        userPushTokenRepository.createUsersPushTokenRecord(pushTokenReqDto.getDeviceType(), userPushTokens);
    }

    @Override
    public void updatePushTokenBind(UserPushTokens userPushTokens) {
        userPushTokenRepository.update(userPushTokens);
    }

    @Override
    public List<UserPushTokensEntity> getAllByUserId(Long userId) {
        return userPushTokenRepository.getAllByUserId(userId);
    }

    @Override
    public void saveOrUpdate(UserPushTokens userPushTokens) {
        userPushTokenRepository.saveOrUpdate(userPushTokens);
    }

    @Override
    public void deleteById(Long id) {
        userPushTokenRepository.deleteById(id);
    }

    @Override
    public void deleteByToken(String token) {
        userPushTokenRepository.deleteByToken(token);
    }

    @Override
    public UserPushTokens findByUserIdAndToken(Long userId, String token) {
        return userPushTokenRepository.findByUserIdAndToken(userId, token)
                .orElseThrow(ResourceNotFoundException::new);
    }
}