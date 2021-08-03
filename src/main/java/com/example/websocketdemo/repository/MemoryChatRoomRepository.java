package com.example.websocketdemo.repository;

import com.example.websocketdemo.model.ChatRoom;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository

public class MemoryChatRoomRepository implements ChatRoomRepository{
    private static final Map<String, ChatRoom> store = new LinkedHashMap<>();


    @Override
    public ChatRoom save(ChatRoom room){
        store.put(room.getRoomId(),room);
        return room;
    }
    @Override
    public List<ChatRoom> findAll(){
        return new ArrayList<>(store.values());
    }



}
