package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatMessage;
import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;

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



        //채팅방 인원 추가
        ChatRoom oldChatRoom = chatRoomService.findById(chatRoomId);
        oldChatRoom.addCount();
        ChatRoom chatRoom = new ChatRoom(oldChatRoom.getName());
        chatRoom.setId(oldChatRoom.getId());
        chatRoom.setCount(oldChatRoom.getCount());
        chatRoom.setExpiryDate(oldChatRoom.getExpiryDate());

        chatRoomService.save(chatRoom);

        // 구독한 채팅방에 입장 메시지 보내기
        headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        template.convertAndSend("/topic/chat/"+chatRoomId, chatMessage);








    }

}