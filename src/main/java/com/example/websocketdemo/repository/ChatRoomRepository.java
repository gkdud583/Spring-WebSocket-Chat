package com.example.websocketdemo.repository;

import com.example.websocketdemo.model.ChatRoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ChatRoomRepository {
    public ChatRoom save(ChatRoom room);
    public List<ChatRoom> findAll();

}
