package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/update")
    @SendTo("/topic/updates")
    public String handleUpdate(String message) {
        System.out.println("Received message: " + message);
        return "Received update: " + message;
    }
}