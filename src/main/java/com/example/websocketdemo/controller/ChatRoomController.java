package com.example.websocketdemo.controller;

import com.example.websocketdemo.service.dto.ChatRoomResponse;
import com.example.websocketdemo.service.ChatRoomService;
import com.example.websocketdemo.service.dto.ChatRoomSaveRequest;
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
    public List<ChatRoomResponse> findAll() {
        return chatRoomService.findAll();
    }

    @PostMapping
    @ResponseBody
    public void addChatRoom(@RequestBody ChatRoomSaveRequest chatRoomSaveRequest) {
        chatRoomService.save(chatRoomSaveRequest.getName());
    }

    //테스트용 데이터 추가
    @PostConstruct
    public void init() {
        chatRoomService.save("test1");
        chatRoomService.save("test2");
        chatRoomService.save("test3");
        chatRoomService.save("test4");
    }
}
