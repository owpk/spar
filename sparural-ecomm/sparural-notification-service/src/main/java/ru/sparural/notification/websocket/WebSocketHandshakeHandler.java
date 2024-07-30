package ru.sparural.notification.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.sparural.notification.security.TokenManager;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {
    private final TokenManager tokenManager;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = httpRequest.getParameter("accessToken");
        var decodedJWT = tokenManager.decodeAndVerifyRawToken(token);
        return new WebSocketPrincipal(decodedJWT);
    }
}