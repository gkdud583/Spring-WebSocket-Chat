package com.example.websocketdemo.controller;

import com.example.websocketdemo.service.dto.ChatRoomResponse;
import com.example.websocketdemo.service.ChatRoomService;
import com.example.websocketdemo.service.dto.ChatRoomSaveRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
}
