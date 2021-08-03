package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate template;

    @MessageMapping("/{chatRoomId}/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @DestinationVariable String chatRoomId

    ) {
        template.convertAndSend("/topic/chat/"+chatRoomId, chatMessage);
    }

    @MessageMapping("/{chatRoomId}/chat.addUser")
    public void addUser(
                               @DestinationVariable String chatRoomId,
                               @Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor
                               ) {

        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        template.convertAndSend("/topic/chat/"+chatRoomId, chatMessage);

    }

}