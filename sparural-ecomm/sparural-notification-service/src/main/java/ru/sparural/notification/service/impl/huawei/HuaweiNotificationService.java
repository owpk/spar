package ru.sparural.notification.service.impl.huawei;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.notification.model.push.PushNotification;
import ru.sparural.notification.service.PushNotificationService;
import ru.sparural.notification.service.impl.huawei.rest.HuaweiRestClient;
import ru.sparural.notification.service.impl.huawei.rest.dto.HuaweiNotificationDto;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HuaweiNotificationService implements PushNotificationService {

    private final HuaweiRestClient huaweiRestClient;
    private final HuaweiTokenRefreshService huaweiTokenRefreshService;

    @Override
    public void send(PushNotification pushNotification, Map<String, String> params) {
        huaweiRestClient.sendPush(
                new HuaweiNotificationDto(pushNotification, params),
                huaweiTokenRefreshService.retrieveServerToken().getAccessToken());
    }
}