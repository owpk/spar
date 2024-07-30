package ru.sparural.notification.service.impl.firebase;

import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FCMService {

    public PushNotificationResponse sendMessageToToken(PushNotificationRequest request, Map<String, String> params)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageBuilder(request, params).build();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = sendAndGetResponse(message);
        log.info("Sent message to token. Device token: " + request.getToken() + ", " + response + " msg " + jsonOutput);
        var pushNotificationResponse = new PushNotificationResponse();
        pushNotificationResponse.setMessage(response);
        pushNotificationResponse.setStatus(1);
        return pushNotificationResponse;
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setTag(topic).build()).build();
    }

    private ApnsConfig getApnsConfig() {
        return ApnsConfig.builder()
                .setAps(Aps.builder().build())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request,
                                                           Map<String, String> params) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig();
        var message = Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getMessage())
                                .build())
                .setToken(request.getToken());
        if (params != null && !params.isEmpty())
            params.forEach(message::putData);
        return message;

    }
}