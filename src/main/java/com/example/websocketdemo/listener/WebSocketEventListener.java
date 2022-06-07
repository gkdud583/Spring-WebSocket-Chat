package com.example.websocketdemo.listener;

import com.example.websocketdemo.service.dto.ChatMessage;
import com.example.websocketdemo.service.ChatRoomService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomService chatRoomService;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, ChatRoomService chatRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.chatRoomService = chatRoomService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String chatRoomId = (String) headerAccessor.getSessionAttributes().get("chatRoomId");

        if (username != null && chatRoomId != null) {
            chatRoomService.exit(chatRoomId);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, chatMessage);
        }
    }
}