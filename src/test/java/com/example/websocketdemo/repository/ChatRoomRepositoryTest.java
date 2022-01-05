package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.ChatRoomInfo;
import com.example.websocketdemo.model.ChatRoom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChatRoomRepositoryTest {
    static ChatRoom chatRoom;

    @Autowired
    ChatRoomRepository chatRoomRepository;


    @BeforeEach
    void beforeEach(){
        chatRoom = ChatRoom.create("test");
        chatRoomRepository.save(ChatRoomInfo.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .count(chatRoom.getCount())
                .expiryDate(chatRoom.getExpiryDate()).build());

    }
    @AfterEach
    void afterEach(){
        chatRoomRepository.deleteAll();
    }
    @Test
    void findById(){
        //given
        //when
        Optional<ChatRoomInfo> findChatRoom = chatRoomRepository.findById(chatRoom.getId());

        //then
        assertThat(findChatRoom.get().getName()).isEqualTo(chatRoom.getName());
    }
    @Test
    void deleteById(){
        //given

        //when
        chatRoomRepository.deleteById(chatRoom.getId());

        //then
        List<ChatRoomInfo> findAll = chatRoomRepository.findAll();
        assertThat(findAll).isEmpty();

    }


}
