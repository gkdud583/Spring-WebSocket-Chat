package com.example.websocketdemo.controller;

import com.example.websocketdemo.service.dto.ChatMessage;
import com.example.websocketdemo.service.ChatRoomService;
import com.sun.istack.NotNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;

    public ChatController(SimpMessagingTemplate template, ChatRoomService chatRoomService) {
        this.template = template;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/{chatRoomId}/chat.sendMessage")
    public void sendMessage(@Payload @Valid ChatMessage chatMessage, @DestinationVariable String chatRoomId) {
        template.convertAndSend("/topic/chat/" + chatRoomId, chatMessage);
    }

    @MessageMapping("/{chatRoomId}/chat.addUser")
    public void addUser(@DestinationVariable String chatRoomId, @Payload @NotNull ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        //채팅방 인원 추가
        chatRoomService.enter(chatRoomId);

        // 구독한 채팅방에 입장 메시지 보내기
        headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        template.convertAndSend("/topic/chat/" + chatRoomId, chatMessage);
    }
}