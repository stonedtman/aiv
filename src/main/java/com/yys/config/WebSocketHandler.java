package com.yys.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class WebSocketHandler extends BinaryWebSocketHandler {

    private ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Consumer<String>> closeListeners = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket 连接已建立，sessionId: " + sessionId);
        session.sendMessage(new TextMessage("{\"type\":\"sessionId\",\"sessionId\":\"" + sessionId + "\"}"));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // Handle binary message if needed
    }

    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void sendFrame(String sessionId, byte[] frameData) throws Exception {
        WebSocketSession session = getSession(sessionId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new BinaryMessage(ByteBuffer.wrap(frameData)));
        } else {
            System.out.println("WebSocket 连接已关闭，无法发送帧数据");
        }
    }

    public void sendTextMessage(String sessionId, String message) throws Exception {
        WebSocketSession session = getSession(sessionId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        System.out.println("WebSocket 连接已关闭，sessionId: " + sessionId);

        Consumer<String> listener = closeListeners.get(sessionId);
        if (listener != null) {
            listener.accept(sessionId);
        }
    }

    public void addCloseListener(String sessionId, Consumer<String> listener) {
        closeListeners.put(sessionId, listener);
    }

    public void removeCloseListener(String sessionId) {
        closeListeners.remove(sessionId);
    }
}




