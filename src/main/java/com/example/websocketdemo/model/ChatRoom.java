package com.example.websocketdemo.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom {
    private static final long chatRoomValidTime = 3;

    private String id;
    private String name;
    private long count;
    private LocalDateTime expiryDate;

    public ChatRoom() {
    }

    public ChatRoom(String name) {
        this.name = name;
    }
    public static ChatRoom create(String name){
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoom.id = UUID.randomUUID().toString();
        chatRoom.count = 0;
        chatRoom.expiryDate = LocalDateTime.now().plusHours(chatRoomValidTime);

        return chatRoom;
    }
    public void minCount(){
        count--;
    }
    public void addCount(){
        count++;
    }

}
