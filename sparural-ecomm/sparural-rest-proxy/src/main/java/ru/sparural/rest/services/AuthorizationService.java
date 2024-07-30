package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sparural.engine.api.dto.user.*;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.JwtResponse;
import ru.sparural.rest.exception.InvalidRefreshSession;
import ru.sparural.rest.redis.RedisUserTokenService;
import ru.sparural.rest.redis.model.UserSession;
import ru.sparural.rest.security.TokenManager;

import javax.servlet.http.HttpServletResponse;
import ru.sparural.engine.api.dto.RecoveryPasswordConfirmCodeRequestDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.RecoveryTokenResponseDto;
import ru.sparural.engine.api.dto.registration.BeginRegistrationRequest;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.rest.config.KafkaTopics;


/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {
    private final TokenManager tokenManager;
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final RedisUserTokenService userTokenService;
    private final KafkaTopics kafkaTopics;

    public JwtResponse loginViaSocials(String social, String code) {
        TokenDataDto tokenData = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("socials/login")
                .withRequestParameter("social", social)
                .withRequestParameter("code", code)
                .sendForEntity();
        return generateJwtResponse(tokenData, false);
    }

    public JwtResponse loginViaOneTimeCode(LoginViaOneTimeCodeUserDataDto loginData) {
        TokenDataDto tokenData = restToKafkaService
                .createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("login/two-fa-confirm")
                .withRequestBody(loginData)
                .sendForEntity();

        return generateJwtResponse(tokenData, true);
    }

    public JwtResponse loginByPhoneAndPassword(LoginUserDataRequestDto phoneLoginRequest,
                                                     String clientType,
                                                     HttpServletResponse response,
                                                     String action) {
        TokenDataDto tokenData = restToKafkaService
                .createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction(action)
                .withRequestBody(phoneLoginRequest)
                .sendForEntity();

        return generateJwtResponse(tokenData, true);
    }

    public JwtResponse refreshTokenRequest(String tokenRefreshRequest) {
        var userSession = userTokenService.getByRefreshToken(tokenRefreshRequest)
                .orElseThrow(() -> new InvalidRefreshSession("Invalid refresh token request"));

        TokenDataDto tokenData = restToKafkaService.sendForEntity(kafkaTopics.getEngineRequestTopicName(), "refresh-token", userSession.getUserId());
        return generateJwtResponse(tokenData, false);
    }

    public JwtResponse confirmPasswordCode(RecoveryPasswordConfirmCodeRequestDto requestDto) {
        TokenDataDto tokenData = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recovery-password-confirm")
                .withRequestBody(requestDto)
                .sendForEntity();
        return generateJwtResponse(tokenData, false);
    }

    public RecoveryTokenResponseDto recoverPassword(RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recovery-password")
                .withRequestBody(recoveryPasswordRequestDto)
                .sendForEntity();
    }

    public JwtResponse beginRegistration(BeginRegistrationRequest beginRegistrationRequest) {
        TokenDataDto tokenData = restToKafkaService
                .createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("registration/begin")
                .withRequestBody(beginRegistrationRequest)
                .sendForEntity();

        return generateJwtResponse(tokenData, true);

    }

    public JwtResponse updateUserInfo(Long userId, UserProfileUpdateRequest userProfileUpdateRequest) {
        TokenDataDto tokenData = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("registration/user")
                .withRequestParameter("userId", userId)
                .withRequestBody(userProfileUpdateRequest)
                .sendForEntity();

        return generateJwtResponse(tokenData, false);
    }

    private JwtResponse generateJwtResponse(TokenDataDto tokenData, boolean needStep) {
        var accessToken = tokenManager.createAccessToken(tokenData);
        var refreshToken = tokenManager.generateRefreshToken();

        var userSession = new UserSession();
        userSession.setUserId(tokenData.getUserId());
        userSession.setAccessToken(accessToken);
        userSession.setRefreshTokenId(refreshToken);
        userSession.setFingerPrint("default");
        userSession.setExpiresIn((System.currentTimeMillis()/1000) + tokenManager.getRefreshExpiresIn());
        userSession.incrementCount();

        userTokenService.validateSessionCount(userSession);
        userTokenService.saveOrUpdate(userSession);

        Integer step;
        if (needStep){
            try {
                step = restToKafkaService
                    .createRequestBuilder()
                    .withTopicName(kafkaTopics.getEngineRequestTopicName())
                    .withAction("registration/check-step")
                    .withRequestParameter("userId", tokenData.getUserId())
                    .sendForEntity();
            } catch (RuntimeException ex) {
                log.error("Error fetch check step", ex);
                step = null;
            }
        } else {
            step = null;
        }

        return new JwtResponse(accessToken, refreshToken, step);
    }

    public TokenDataDto refreshTokenRequest(Long userId) {
        return restToKafkaService.sendForEntity(kafkaTopics.getEngineRequestTopicName(), "refresh-token", userId);
    }

    public CodeLoginResponse exchangeForOneTimeCode(String login) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("login/two-fa")
                .withRequestParameter("login", login)
                .sendForEntity();
    }

}