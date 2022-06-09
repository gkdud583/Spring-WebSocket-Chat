package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.ChatRoom;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.service.dto.ChatRoomResponse;
import com.example.websocketdemo.entity.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    private final ChatRoom chatRoom = new ChatRoom("test");

    @Test
    void saveSuccess() {
        //given
        String name = "test";

        //then
        chatRoomService.save(name);

        //then
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void saveFailWithNameIsNullOrBlank(String name) {
        //given
        //when, then
        assertThatThrownBy(() ->
                chatRoomService.save(name))
                .isInstanceOf(CustomException.class)
                .hasMessage("유효하지 않은 채팅방 이름입니다.");
    }

    @Test
    void findAllSuccess() {
        //given
        List<ChatRoom> chatRooms = Arrays.asList(new ChatRoom("test"));
        given(chatRoomRepository.findAll())
                .willReturn(chatRooms);

        //when
        List<ChatRoomResponse> result = chatRoomService.findAll();

        //then
        verify(chatRoomRepository).findAll();
        assertThat(result.size()).isEqualTo(chatRooms.size());
    }

    @Test
    void findAllWithChatRoomListSizeZero() {
        //given
        List<ChatRoom> chatRooms = Collections.emptyList();
        given(chatRoomRepository.findAll())
                .willReturn(chatRooms);

        //when
        List<ChatRoomResponse> result = chatRoomService.findAll();

        //then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void deleteByCreatedDateLessThanEqualSuccess() {
        //given
        ChatRoom createdChatRoom = new ChatRoom("test");
        ReflectionTestUtils.setField(createdChatRoom, "expiryDate", LocalDateTime.now().minusHours(4));
        List<ChatRoom> chatRooms = Arrays.asList(createdChatRoom, chatRoom);
        given(chatRoomRepository.findAll())
                .willReturn(chatRooms);

        //when
        chatRoomService.deleteByCreatedDateLessThanEqual();

        //then
        verify(chatRoomRepository, times(1)).findAll();
        verify(chatRoomRepository, times(1)).deleteById(anyString());
    }

    @Test
    void enterSuccess() {
        //given
        ChatRoom createdChatRoom = new ChatRoom("test");
        given(chatRoomRepository.findById(anyString()))
                .willReturn(Optional.of(createdChatRoom));

        //when
        chatRoomService.enter(createdChatRoom.getId());

        //then
        verify(chatRoomRepository).findById(anyString());
        assertThat(createdChatRoom.getCount()).isEqualTo(1);
    }

    @Test
    void enterNonExistentChatRoom() {
        //given
        String id = "NonExistentChatRoomId";
        given(chatRoomRepository.findById(anyString()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() ->
                chatRoomService.enter(id))
                .isInstanceOf(CustomException.class)
                .hasMessage("채팅방을 찾을 수 없습니다.");
    }

    @Test
    void exitSuccess() {
        //given
        ChatRoom createdChatRoom = new ChatRoom("test");
        createdChatRoom.enter();
        given(chatRoomRepository.findById(anyString()))
                .willReturn(Optional.of(createdChatRoom));

        //when
        chatRoomService.exit(createdChatRoom.getId());

        //then
        verify(chatRoomRepository).findById(anyString());
        assertThat(createdChatRoom.getCount()).isEqualTo(0);
    }

    @Test
    void exitSuccessWithNegativeCount() {
        //given
        ChatRoom createdChatRoom = new ChatRoom("test");
        given(chatRoomRepository.findById(anyString()))
                .willReturn(Optional.of(createdChatRoom));

        //when
        chatRoomService.exit(createdChatRoom.getId());

        //then
        verify(chatRoomRepository).findById(anyString());
        assertThat(createdChatRoom.getCount()).isEqualTo(0);
    }

    @Test
    void exitNonExistentChatRoom() {
        //given
        String id = "NonExistentChatRoomId";
        given(chatRoomRepository.findById(anyString()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() ->
                chatRoomService.exit(id))
                .isInstanceOf(CustomException.class)
                .hasMessage("채팅방을 찾을 수 없습니다.");
    }
}
