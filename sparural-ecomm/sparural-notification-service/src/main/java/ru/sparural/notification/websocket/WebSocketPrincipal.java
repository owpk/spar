package ru.sparural.notification.websocket;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.Principal;
import java.util.Objects;

public class WebSocketPrincipal implements Principal {
    private final DecodedJWT token;
    private final String userId;

    public WebSocketPrincipal(DecodedJWT token) {
        this.token = token;
        this.userId = token.getSubject();
    }


    @Override
    public String getName() {
        return userId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.userId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebSocketPrincipal other = (WebSocketPrincipal) obj;
        return Objects.equals(this.userId, other.userId);
    }
}
