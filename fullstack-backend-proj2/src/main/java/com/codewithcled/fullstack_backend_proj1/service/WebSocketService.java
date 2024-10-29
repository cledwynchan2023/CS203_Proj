package com.codewithcled.fullstack_backend_proj1.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewithcled.fullstack_backend_proj1.DTO.ResultRequest;
import com.codewithcled.fullstack_backend_proj1.components.WebSocketClient;


// WebSocketService.java
@Service
public class WebSocketService {

    private final WebSocketClient webSocketClient;

    
    public WebSocketService(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public void broadcastUpdate(int result) throws InterruptedException, ExecutionException {
        webSocketClient.connect();
        webSocketClient.sendUpdate(result);
    }
}
