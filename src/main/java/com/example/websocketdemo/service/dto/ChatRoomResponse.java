package com.example.websocketdemo.service.dto;

import com.example.websocketdemo.entity.ChatRoom;
import java.time.LocalDateTime;

public class ChatRoomResponse {
    private final String id;
    private final String name;
    private final int count;
    private final LocalDateTime expiryDate;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.name = chatRoom.getName();
        this.count = chatRoom.getCount();
        this.expiryDate = chatRoom.getExpiryDate();
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
