package com.example.RealTimeTicketing.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LogWebSocketHandler logWebSocketHandler;

    /**
     * Constructs a new WebSocketConfig instance with the given WebSocket handler.
     *
     * @param logWebSocketHandler The WebSocket handler for logging.
     */
    public WebSocketConfig(LogWebSocketHandler logWebSocketHandler) {
        this.logWebSocketHandler = logWebSocketHandler;
    }

    /**
     * Registers the WebSocket handler for the `/logs` endpoint.
     *
     * @param registry The WebSocketHandlerRegistry to register the handler.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(logWebSocketHandler, "/logs").setAllowedOrigins("*");
    }
}
