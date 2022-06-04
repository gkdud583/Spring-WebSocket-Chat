package com.example.websocketdemo.service;


import com.example.websocketdemo.entity.ChatRoomInfo;
import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public void save(com.example.websocketdemo.model.ChatRoom chatRoom){
        chatRoomRepository.save(new ChatRoomInfo(chatRoom.getId(), chatRoom.getName(), chatRoom.getCount(), chatRoom.getExpiryDate()));


    }
    public List<com.example.websocketdemo.model.ChatRoom> findAll(){
        List<com.example.websocketdemo.model.ChatRoom> chatRooms = new LinkedList<>();
        chatRoomRepository.findAll().forEach(chatRoomInfo -> {
            com.example.websocketdemo.model.ChatRoom chatRoom = new ChatRoom(chatRoomInfo.getId(), chatRoomInfo.getName(), chatRoomInfo.getCount(), chatRoomInfo.getExpiryDate());

            chatRooms.add(chatRoom);
        });
        return chatRooms;
    }

    public com.example.websocketdemo.model.ChatRoom findById(String id){
        Optional<ChatRoomInfo> findChatRoom = chatRoomRepository.findById(id);
        if(findChatRoom.isPresent()){
            ChatRoomInfo chatRoomInfo = findChatRoom.get();
            com.example.websocketdemo.model.ChatRoom chatRoom = new ChatRoom(chatRoomInfo.getId(), chatRoomInfo.getName(), chatRoomInfo.getCount(), chatRoomInfo.getExpiryDate());

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
