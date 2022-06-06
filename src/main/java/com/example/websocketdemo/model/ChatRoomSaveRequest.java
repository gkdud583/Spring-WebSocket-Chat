package com.example.websocketdemo.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ChatRoomSaveRequest {
    private final String name;

    @JsonCreator
    public ChatRoomSaveRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
