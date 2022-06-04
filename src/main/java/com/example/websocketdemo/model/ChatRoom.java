package com.example.websocketdemo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatRoom {
    private static final long chatRoomValidTime = 3;

    private String id;
    private String name;
    private long count;
    private LocalDateTime expiryDate;

    public ChatRoom(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.count = 0;
        this.expiryDate = LocalDateTime.now().plusHours(chatRoomValidTime);
    }

    public ChatRoom(String id, String name, long count, LocalDateTime expiryDate) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void minCount(){
        count--;
    }
    public void addCount(){
        count++;
    }

}
