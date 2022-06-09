package com.example.websocketdemo.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomTest {
    @Test
    void enterSuccess() {
        //given
        ChatRoom chatRoom = new ChatRoom("test");

        //when
        chatRoom.enter();

        //then
        assertThat(chatRoom.getCount()).isEqualTo(1);
    }

    @Test
    void exitSuccess() {
        //given
        ChatRoom chatRoom = new ChatRoom("test");

        //when
        chatRoom.exit();

        //then
        assertThat(chatRoom.getCount()).isEqualTo(0);
    }

    @Test
    void isRemovableSuccess1() {
        //given
        ChatRoom chatRoom = new ChatRoom("test");

        //when
        boolean removable = chatRoom.isRemovable();

        //then
        assertThat(removable).isFalse();
    }

    @Test
    void isRemovableSuccess2() {
        //given
        ChatRoom chatRoom = new ChatRoom("test");
        ReflectionTestUtils.setField(chatRoom, "expiryDate", LocalDateTime.now().minusHours(5));

        //when
        boolean removable = chatRoom.isRemovable();

        //then
        assertThat(removable).isTrue();
    }
}