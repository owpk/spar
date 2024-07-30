package ru.sparural.notification.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.sparural.notification.api.dto.ws.WSNotificationRequestDto;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketPushService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendPush(Long userId, WSNotificationRequestDto payload) {
        simpMessagingTemplate.convertAndSendToUser(userId.toString(), WebSocketConfig.BROKER, payload);
        log.info("Send websocket message: user_id: {}, body: {}", userId, payload);
    }
}