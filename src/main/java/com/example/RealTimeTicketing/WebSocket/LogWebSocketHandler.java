package com.example.RealTimeTicketing.WebSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket handler for handling real-time log messages.
 * It manages WebSocket sessions, handles incoming messages, and broadcasts log messages to the frontend.
 */
@Component
public class LogWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * Handles the establishment of a new WebSocket connection.
     * Adds the newly created session to the list of active sessions.
     *
     * @param session The newly established WebSocket session.
     * @throws Exception If an error occurs during connection establishment.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    /**
     * Handles the closing of a WebSocket connection.
     * Removes the closed session from the list of active sessions.
     *
     * @param session The closed WebSocket session.
     * @param status The status code indicating the reason for closure.
     * @throws Exception If an error occurs during connection closure.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    /**
     * Broadcasts a log message to all active WebSocket sessions.
     *
     * @param logMessage The log message to be broadcast.
     */
    public void sendLog(String logMessage) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(logMessage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}