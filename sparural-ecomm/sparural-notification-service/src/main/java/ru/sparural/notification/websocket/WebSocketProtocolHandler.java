package ru.sparural.notification.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketProtocolHandler {

    private final Set<String> sessionStore = ConcurrentHashMap.newKeySet();

    @EventListener
    public void afterConnectionEstablished(SessionConnectedEvent session) {
        WebSocketPrincipal wsUser = (WebSocketPrincipal) session.getUser();
        if (wsUser == null) {
            log.error("Connected session without principal");
            return;
        }
        log.info("User passed handshake phase: " + wsUser.getName());
        sessionStore.add(wsUser.getName());
    }

    @EventListener
    public void afterConnectionClosed(SessionDisconnectEvent session) {
        WebSocketPrincipal wsUser = (WebSocketPrincipal) session.getUser();
        if (wsUser == null) {
            log.error("Connected session without principal");
            return;
        }
        log.info("User disconnected: " + wsUser.getName());
        sessionStore.remove(wsUser.getName());
    }

    public boolean isUserConnected(Long userId) {
        return sessionStore.contains(userId.toString());
    }
}
