package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.ChatRoom;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.service.dto.ChatRoomResponse;
import com.example.websocketdemo.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import static com.example.websocketdemo.exception.ErrorCode.INVALID_CHAT_ROOM_NAME;
import static com.example.websocketdemo.exception.ErrorCode.NOT_FOUND_CHAT_ROOM;
import static java.time.LocalDateTime.now;

@Service
@Transactional
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public void save(String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(INVALID_CHAT_ROOM_NAME);
        }
        chatRoomRepository.save(new ChatRoom(name));
    }

    public List<ChatRoomResponse> findAll() {
        return chatRoomRepository.findAll().stream()
                                 .map((chatRoom) -> new ChatRoomResponse(chatRoom))
                                 .collect(Collectors.toList());
    }

    public void deleteByCreatedDateLessThanEqual() {
        chatRoomRepository.findAll().stream().forEach((chatRoom -> {
            if (chatRoom.isRemovable()) {
                chatRoomRepository.deleteById(chatRoom.getId());
            }
        }));
    }

    public void enter(String id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                                              .orElseThrow(() -> new CustomException(NOT_FOUND_CHAT_ROOM));
        chatRoom.enter();
    }

    public void exit(String id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                                              .orElseThrow(() -> new CustomException(NOT_FOUND_CHAT_ROOM));
        chatRoom.exit();
    }
}
