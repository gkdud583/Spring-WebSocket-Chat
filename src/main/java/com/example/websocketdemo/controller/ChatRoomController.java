package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RequestMapping("api/v1/chatRooms")
@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping
    @ResponseBody
    public List<ChatRoom> findChatRooms() {
        List<ChatRoom> rooms = chatRoomService.findAll();
        return rooms;
    }

    @PostMapping
    @ResponseBody
    public ChatRoom addChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        chatRoomService.save(chatRoom);

        return chatRoom;
    }

    //테스트용 데이터 추가
    @PostConstruct
    public void init() {
        chatRoomService.save(ChatRoom.create("test1"));
        chatRoomService.save(ChatRoom.create("test2"));
        chatRoomService.save(ChatRoom.create("test3"));
        chatRoomService.save(ChatRoom.create("test4"));
    }
}
