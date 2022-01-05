package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.UserInfo;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void save(){
        //given
        User user = new User("test@naver.com", "zns9dyek951956");

        //when
        userService.save(user);

        //then
        verify(userRepository).save(any());
    }


    @Test
    void isExistEmail(){
        //given
        User user = new User("test@naver.com", "zns9dyek951956");
        UserInfo userInfo = UserInfo.builder()
                .email(user.getEmail())
                .auth(user.getAuth())
                .password(new BCryptPasswordEncoder().encode(user.getPassword())).build();
        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(userInfo));


        //when
        userService.isExistEmail(user.getEmail());

        //then
        verify(userRepository).findByEmail(any());

    }
}
