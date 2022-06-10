package com.example.websocketdemo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.Duration;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ChatRoomStateServiceTest.Config.class)
class ChatRoomStateServiceTest {
    @SpyBean
    ChatRoomStateService chatRoomStateService;

    @Configuration
    @EnableScheduling
    public static class Config {
        @Bean
        ChatRoomStateService chatRoomStateService() {
            return new ChatRoomStateService(mock(ChatRoomService.class));
        }
    }

    @Test
    void whenWaitOneSecondThenDeleteRoomsIsCalledAtLeastOneTimes() {
        await()
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(chatRoomStateService, atLeast(1)).deleteRooms());
    }
}