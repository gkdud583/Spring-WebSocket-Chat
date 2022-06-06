package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findById(String id);

    void deleteById(String id);
}
