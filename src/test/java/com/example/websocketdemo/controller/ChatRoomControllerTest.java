package com.example.websocketdemo.controller;

import com.example.websocketdemo.entity.ChatRoom;
import com.example.websocketdemo.service.dto.ChatRoomResponse;
import com.example.websocketdemo.service.dto.ChatRoomSaveRequest;
import com.example.websocketdemo.service.ChatRoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatRoomController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class),}
)
public class ChatRoomControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatRoomService chatRoomService;

    private static final String BASE_URL = "/api/v1/chatRooms";
    private static final String FIND_ALL_URL = BASE_URL;
    private static final String SAVE_URL = BASE_URL;

    @Test
    @WithMockUser
    void findChatRoomsSuccess() throws Exception {
        //given
        List<ChatRoomResponse> chatRoomResponses = Arrays.asList(
                new ChatRoomResponse(new ChatRoom("test1")),
                new ChatRoomResponse(new ChatRoom("test2")));
        given(chatRoomService.findAll())
                .willReturn(chatRoomResponses);

        //when, then
        mvc.perform(get(FIND_ALL_URL))
           .andExpect(status().isOk())
           .andExpect(content().json(toJson(chatRoomResponses)));
    }

    @Test
    @WithMockUser
    void findChatRoomsWithChatRoomListSizeZero() throws Exception {
        //given
        List<ChatRoomResponse> chatRoomResponses = Collections.emptyList();
        given(chatRoomService.findAll())
                .willReturn(chatRoomResponses);

        //when, then
        mvc.perform(get(FIND_ALL_URL))
           .andExpect(status().isOk())
           .andExpect(content().json(toJson(chatRoomResponses)));
    }

    @Test
    @WithMockUser
    void addChatRoomSuccess() throws Exception {
        //given
        ChatRoomSaveRequest request = new ChatRoomSaveRequest("test");

        //when, then
        mvc.perform(post(SAVE_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf())
           .contentType(APPLICATION_JSON)
           .content(toJson(request)))
           .andExpect(status().isOk());

        //then
        verify(chatRoomService).save(anyString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @WithMockUser
    void addChatRoomFailWithChatRoomNameIsBlankOrNull(String name) throws Exception {
        //given
        ChatRoomSaveRequest request = new ChatRoomSaveRequest(name);

        //when, then
        MvcResult mvcResult = mvc.perform(post(SAVE_URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(APPLICATION_JSON)
                        .content(toJson(request)))
                        .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(mvcResult.getResolvedException().getMessage()).contains("채팅방 이름은 필수 값입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"The name of the chat room is more than 26 characters long."})
    @WithMockUser
    void addChatRoomFailWithChatRoomNameIsMoreThan26CharactersLong(String name) throws Exception {
        //given
        ChatRoomSaveRequest request = new ChatRoomSaveRequest(name);

        //when, then
        MvcResult mvcResult = mvc.perform(post(SAVE_URL)
                                 .with(SecurityMockMvcRequestPostProcessors.csrf())
                                 .contentType(APPLICATION_JSON)
                                 .content(toJson(request)))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(BAD_REQUEST.value());
        assertThat(mvcResult.getResolvedException().getMessage()).contains("채팅방 이름의 길이는 최대 23자입니다.");
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
