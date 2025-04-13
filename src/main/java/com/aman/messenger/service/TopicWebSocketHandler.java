package com.aman.messenger.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class TopicWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> customSessionMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> reverseMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        try {
            JSONObject json = new JSONObject(payload);
            if (json.has("type") && json.getString("type").equals("init")) {
                // Register custom name
                String customName = json.getString("customName");
                customSessionMap.put(customName, session);
                reverseMap.put(session, customName);
                System.out.println("Session named: " + customName);
                return; // don't broadcast this init message
            }
        } catch (Exception e) {
            // ignore if not JSON, proceed with broadcasting
        }

        // Broadcast message
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                String name = reverseMap.getOrDefault(session, session.getId());
                s.sendMessage(new TextMessage(name + ": " + payload));
            }
        }
    }

    public int getActiveConnectionCount() {
        return sessions.size(); // Return the size of the active sessions list
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        String name = reverseMap.remove(session);
        if (name != null) {
            customSessionMap.remove(name);
        }
    }
}
