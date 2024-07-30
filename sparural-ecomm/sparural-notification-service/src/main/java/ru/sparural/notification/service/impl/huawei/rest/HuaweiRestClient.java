package ru.sparural.notification.service.impl.huawei.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sparural.notification.service.impl.huawei.HuaweiRestConstants;
import ru.sparural.notification.service.impl.huawei.rest.dto.HuaweiNotificationDto;
import ru.sparural.notification.service.impl.huawei.rest.dto.TokenResponseDto;
import ru.sparural.utils.RestTemplateConstants;
import ru.sparural.utils.rest.AuthType;
import ru.sparural.utils.rest.RestTemplate;

import java.text.MessageFormat;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HuaweiRestClient {

    private final RestTemplate restTemplate;
    @Value("${huawei.app_token}")
    private String appToken;
    @Value("${huawei.app_id}")
    private String appId;

    public void sendPush(HuaweiNotificationDto huaweiNotificationDto, String accessToken) {
        String url = MessageFormat.format(HuaweiRestConstants.SEND_PUSH, appId);
        restTemplate.request()
                .withAuthorizationHeader(AuthType.BEARER, accessToken)
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_JSON_UTF_8)
                .post(url, huaweiNotificationDto);
    }

    public TokenResponseDto refreshToken() {
        var body = MessageFormat.format(
                "grant_type=client_credentials&client_secret={0}&client_id={1}",
                appToken, appId);
        return restTemplate.request()
                .withFailureCallback(resp -> {
                    log.error("Fail to refresh huawei app token: code: {}, message: {}, full: {}",
                            resp.getCode(), resp.getMsg(), new String(resp.getBody()));
                    throw throwRefreshTokenFail();
                })
                .withContentType(RestTemplateConstants.MediaTypes.APPLICATION_FORM_URLENCODED_UTF_8)
                .setResponseType(TokenResponseDto.class)
                .postForEntity(HuaweiRestConstants.HUAWEI_TOKEN, body)
                .orElseThrow(this::throwRefreshTokenFail);
    }

    private RuntimeException throwRefreshTokenFail() {
        return new RuntimeException("Cannot refresh huawei app token");
    }
}