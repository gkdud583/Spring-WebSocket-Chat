package com.example.websocketdemo.model;

import java.util.UUID;

public class ChatRoom {
    private String roomId;
    private String name;

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static ChatRoom create(String name){
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoom.setRoomId(UUID.randomUUID().toString());

        return chatRoom;
    }

}
