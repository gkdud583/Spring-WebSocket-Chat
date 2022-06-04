package com.example.websocketdemo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatRoomStateService {
    private final ChatRoomService chatRoomService;

    public ChatRoomStateService(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @Scheduled(cron = "10 * * * * *")
    public void deleteRooms(){
        chatRoomService.deleteByCreatedDateLessThanEqual();
    }

}
