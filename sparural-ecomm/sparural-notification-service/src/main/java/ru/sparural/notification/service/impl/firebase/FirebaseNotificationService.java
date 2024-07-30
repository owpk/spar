package ru.sparural.notification.service.impl.firebase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.notification.api.dto.push.PushNotificationDto;
import ru.sparural.notification.model.push.PushNotification;
import ru.sparural.notification.service.PushNotificationService;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseNotificationService implements PushNotificationService {

    private final FCMService fcmService;

    @Override
    public void send(PushNotification pushNotification, Map<String, String> params) throws Exception {
        var fcmPushRequest = new PushNotificationRequest();
        fcmPushRequest.setToken(pushNotification.getPushToken());
        fcmPushRequest.setMessage(pushNotification.getBody());
        fcmPushRequest.setTitle(pushNotification.getTitle());
        var response = fcmService.sendMessageToToken(fcmPushRequest, params);
        log.info("Firebase message sent : [{}] Status: [msg: {}, status: {}]" , pushNotification, response.getMessage(), response.getStatus());
    }
}
