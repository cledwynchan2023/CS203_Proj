package com.codewithcled.fullstack_backend_proj1.components;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class WebSocketClient {

    private StompSession stompSession;
    private final WebSocketStompClient stompClient;

    public WebSocketClient() {
        // Create a new WebSocketStompClient using StandardWebSocketClient
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        
        // Use MappingJackson2MessageConverter to convert JSON messages
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }
   
    
    @Async
    @SuppressWarnings("Deprecated")
    public void connect() {
        System.out.println("Connecting to WebSocket server...");
        try {
            this.stompSession = stompClient.connect("ws://localhost:8082/ws", new MyStompSessionHandler()).get();
            System.out.println("WebSocket connection established.");
        } catch (Exception e) {
            System.out.println("Failed to connect to WebSocket server");
            System.err.println("Failed to establish WebSocket connection: " + e.getMessage());
        }
    }
    public boolean isConnected() {
        return stompSession != null && stompSession.isConnected();
    }

    public void sendUpdate(int result) {
        System.out.println(isConnected());
        if (stompSession != null && stompSession.isConnected()) {
            System.out.println("Sending update to WebSocket server: " + result);
            stompSession.send("/app/update", String.valueOf(result));
        } else {
            System.err.println("WebSocket session is not connected.");
        }
    }
    private class MyStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("New WebSocket session established: " + session.getSessionId());
            
            // Subscribe to a topic
            session.subscribe("/topic/updates", this);
            System.out.println("Subscribed to /topic/updates");

            // Send a test message (if needed)
            session.send("/app/hello", "Hello from client");
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            System.out.println("Received: " + payload);
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class; // Adjust based on payload type (e.g., JSON)
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            System.err.println("Error in WebSocket session: " + exception.getMessage());
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            System.err.println("Transport error: " + exception.getMessage());
        }
    }
}