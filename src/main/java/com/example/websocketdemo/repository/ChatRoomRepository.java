package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.ChatRoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


public interface ChatRoomRepository extends JpaRepository<ChatRoomInfo, Long> {
    Optional<ChatRoomInfo> findById(String id);
    void deleteById(String id);

}
