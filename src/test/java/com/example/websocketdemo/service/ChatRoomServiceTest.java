package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.ChatRoomInfo;
import com.example.websocketdemo.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @InjectMocks
    private ChatRoomService chatRoomService;



    @Test
    void save(){
        //given
        com.example.websocketdemo.model.ChatRoom chatRoom = com.example.websocketdemo.model.ChatRoom.create("test");
        //when
        chatRoomService.save(chatRoom);
        //then
        verify(chatRoomRepository).save(any());

    }

    @Test
    void findAll(){
        //given
        List<ChatRoomInfo> result = new LinkedList<>();

        when(chatRoomRepository.findAll())
                .thenReturn(result);

        //when
        chatRoomService.findAll();
        //then
        verify(chatRoomRepository).findAll();
    }

    @Test
    void findById(){
        //given
        String name = "test";

        com.example.websocketdemo.model.ChatRoom chatRoom = com.example.websocketdemo.model.ChatRoom.create(name);
        ChatRoomInfo chatRoomInfo = ChatRoomInfo.builder()
                                                .id(chatRoom.getId())
                                                .name(chatRoom.getName())
                                                .count(chatRoom.getCount())
                                                .expiryDate(chatRoom.getExpiryDate())
                                                .build();

        doReturn(Optional.of(chatRoomInfo))
                .when(chatRoomRepository).findById(chatRoom.getId());

        //when
        chatRoomService.findById(chatRoom.getId());

        //then
        verify(chatRoomRepository, times(1)).findById(anyString());
    }

    @Test
    void deleteByCreatedDateLessThanEqual(){
        //given
        List<ChatRoomInfo> result = new LinkedList<>();
        given(chatRoomRepository.findAll())
                .willReturn(result);

        //when
        chatRoomService.deleteByCreatedDateLessThanEqual();

        //then
        verify(chatRoomRepository, times(1)).findAll();
        verify(chatRoomRepository, times(0)).deleteById(anyString());

    }

}
