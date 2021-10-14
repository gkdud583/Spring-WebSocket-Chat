package com.example.websocketdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatRoomStateService {
    private final ChatRoomService chatRoomService;

    @Scheduled(cron = "10 * * * * *")
    public void deleteRooms(){
        log.info("deleteRooms");
        chatRoomService.deleteByCreatedDateLessThanEqual();
    }

}
