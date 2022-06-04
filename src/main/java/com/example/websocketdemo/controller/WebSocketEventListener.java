package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomService chatRoomService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String)headerAccessor.getSessionAttributes().get("username");
        String chatRoomId = (String)headerAccessor.getSessionAttributes().get("chatRoomId");

        if(username != null && chatRoomId != null) {
            ChatRoom oldChatRoom = chatRoomService.findById(chatRoomId);
            oldChatRoom.minCount();
            ChatRoom chatRoom = new ChatRoom(oldChatRoom.getName());
            chatRoom.setId(oldChatRoom.getId());
            chatRoom.setCount(oldChatRoom.getCount());
            chatRoom.setExpiryDate(oldChatRoom.getExpiryDate());
            chatRoomService.save(chatRoom);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/chat/"+chatRoomId, chatMessage);
        }
    }
}