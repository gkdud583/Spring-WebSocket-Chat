package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.service.ChatRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;

@RequestMapping("api/v1/chatRooms")
@Controller
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping
    @ResponseBody
    public List<ChatRoom> findChatRooms() {
        List<ChatRoom> rooms = chatRoomService.findAll();
        return rooms;
    }

    @PostMapping
    @ResponseBody
    public ChatRoom addChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoomService.save(chatRoom);

        return chatRoom;
    }

    //테스트용 데이터 추가
    @PostConstruct
    public void init() {
        chatRoomService.save(new ChatRoom("test1"));
        chatRoomService.save(new ChatRoom("test2"));
        chatRoomService.save(new ChatRoom("test3"));
        chatRoomService.save(new ChatRoom("test4"));
    }
}
