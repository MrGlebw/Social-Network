package com.gleb.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public void addUserSession(String username, WebSocketSession session) {
        userSessions.put(username, session);
    }

    public WebSocketSession getUserSession(String username) {
        return userSessions.get(username);
    }

    public void removeUserSession(String username) {
        userSessions.remove(username);
    }

    public Map<String, WebSocketSession> getUserSessions() {
        return userSessions;
    }
}

