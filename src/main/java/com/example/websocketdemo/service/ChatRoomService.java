package com.example.websocketdemo.service;


import com.example.websocketdemo.entity.ChatRoomInfo;
import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public void save(ChatRoom chatRoom){
        chatRoomRepository.save(ChatRoomInfo.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .count(chatRoom.getCount())
                .expiryDate(chatRoom.getExpiryDate()).build());


    }
    public List<ChatRoom> findAll(){
        List<ChatRoom> chatRooms = new LinkedList<>();
        chatRoomRepository.findAll().forEach(chatRoomInfo -> {
            ChatRoom chatRoom = new ChatRoom(chatRoomInfo.getName());
            chatRoom.setId(chatRoomInfo.getId());
            chatRoom.setCount(chatRoomInfo.getCount());
            chatRoom.setExpiryDate(chatRoomInfo.getExpiryDate());

            chatRooms.add(chatRoom);
        });
        return chatRooms;
    }

    public ChatRoom findById(String id){
        Optional<ChatRoomInfo> findChatRoom = chatRoomRepository.findById(id);
        if(findChatRoom.isPresent()){
            ChatRoomInfo chatRoomInfo = findChatRoom.get();
            ChatRoom chatRoom = new ChatRoom(chatRoomInfo.getName());
            chatRoom.setId(chatRoomInfo.getId());
            chatRoom.setCount(chatRoomInfo.getCount());
            chatRoom.setExpiryDate(chatRoomInfo.getExpiryDate());

            return chatRoom;

        }
        return null;

    }
    public void deleteByCreatedDateLessThanEqual(){
        LocalDateTime now = LocalDateTime.now();

        List<ChatRoomInfo> findChatRooms = chatRoomRepository.findAll();

        if(findChatRooms != null){
            for(int i=0; i<findChatRooms.size(); i++){
                ChatRoomInfo chatRoomInfo = findChatRooms.get(i);
                if (chatRoomInfo.getExpiryDate().isBefore(now) && chatRoomInfo.getCount() == 0) {

                    chatRoomRepository.deleteById(chatRoomInfo.getId());


                }

            }
        }

    }

}
