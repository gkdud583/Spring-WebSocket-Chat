package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.ChatRoom;
import com.example.websocketdemo.service.ChatRoomService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class ChatRoomControllerTest {
    private MockMvc mvc;

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatRoomController chatRoomController;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup(){

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(chatRoomController)
                .build();
    }

    @Test
    void findChatRooms() throws Exception{

        //given
        LinkedList<ChatRoom> result = new LinkedList<>();
        result.add(ChatRoom.create("test1"));
        result.add(ChatRoom.create("test2"));
        result.add(ChatRoom.create("test3"));


        given(chatRoomService.findAll())
                .willReturn(result);
        //when
        MockHttpServletResponse response = mvc.perform(
                get("/chatRooms"))
                .andReturn().getResponse();


        //then
        assertThat(response.getStatus()).isEqualTo(200);

        List<ChatRoom> chatRooms = mapper.readValue(response.getContentAsString(), new TypeReference<List<ChatRoom>>() {
        });

        assertThat(chatRooms.size()).isEqualTo(result.size());

    }

    @Test
    void addChatRoom() throws Exception{
        //given

        //when
        MockHttpServletResponse response = mvc.perform(post("/add/room")
                .param("name", "test"))
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(200);

        ChatRoom chatRoom = mapper.readValue(response.getContentAsString(), new TypeReference<ChatRoom>() {
        });

        assertThat(chatRoom.getName()).isEqualTo("test");


    }

}
