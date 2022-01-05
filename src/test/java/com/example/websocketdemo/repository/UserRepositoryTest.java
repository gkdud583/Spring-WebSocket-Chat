package com.example.websocketdemo.repository;


import com.example.websocketdemo.entity.UserInfo;

import com.example.websocketdemo.model.User;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    static User user;
    @Autowired
    private UserRepository userRepository;



    @BeforeEach
    void beforeEach(){
        user = new User("test@naver.com","test123456789");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(UserInfo.builder()
                .email(user.getEmail())
                .auth(user.getAuth())
                .password(user.getPassword()).build());

    }
    @AfterEach
    void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    void findByEmail(){

        Optional<UserInfo> findUserInfo = userRepository.findByEmail(user.getEmail());

        assertThat(findUserInfo.get().getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void deleteAll(){

        userRepository.deleteAll();
        List<UserInfo> userInfoList = userRepository.findAll();
        assertThat(userInfoList).isEmpty();

    }

    @Test
    void findAll(){
        List<UserInfo> userInfoList = userRepository.findAll();
        assertThat(userInfoList.size()).isEqualTo(1);
    }
}
