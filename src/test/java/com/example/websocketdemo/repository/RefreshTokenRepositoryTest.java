package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.RefreshTokenInfo;
import com.example.websocketdemo.model.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RefreshTokenRepositoryTest {
    static RefreshToken refreshToken;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void beforeEach(){
        refreshToken = RefreshToken.create("test@naver.com");
        refreshTokenRepository.save(RefreshTokenInfo.builder()
                .email(refreshToken.getEmail())
                .token(refreshToken.getToken())
                .expiryDate(refreshToken.getExpiryDate()).build());

    }
    @AfterEach
    void afterEach() {
        refreshTokenRepository.deleteAll();
    }
    @Test
    void findByToken(){
        //given
        //when
        Optional<RefreshTokenInfo> findRefreshTokenInfo = refreshTokenRepository.findByToken(refreshToken.getToken());

        //then
        assertThat(findRefreshTokenInfo.get().getEmail()).isEqualTo(refreshToken.getEmail());

    }

    @Test
    void deleteByToken(){
        //given
        //when
        refreshTokenRepository.deleteByToken(refreshToken.getToken());

        //then
        List<RefreshTokenInfo> findAll = refreshTokenRepository.findAll();
        assertThat(findAll).isEmpty();
    }
}
