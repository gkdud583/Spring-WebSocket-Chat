package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.RefreshTokenInfo;
import com.example.websocketdemo.model.RefreshToken;
import com.example.websocketdemo.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void createRefreshToken(){
        //givn
        RefreshToken refreshToken = RefreshToken.create("test");

        //when
        refreshTokenService.createRefreshToken(refreshToken);

        //then
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void findByToken(){
        //given
        RefreshToken refreshToken = RefreshToken.create("test@naver.com");
        RefreshTokenInfo refreshTokenInfo = RefreshTokenInfo.builder()
                                            .email(refreshToken.getEmail())
                                            .token(refreshToken.getToken())
                                            .expiryDate(refreshToken.getExpiryDate()).build();
        given(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .willReturn(Optional.of(refreshTokenInfo));

        //when
        refreshTokenService.findByToken(refreshToken.getToken());

        //then
        verify(refreshTokenRepository).findByToken(any());

    }

    @Test
    void verifyExpiration(){
        //given
        RefreshToken refreshToken = RefreshToken.create("test");
        refreshTokenService.createRefreshToken(refreshToken);

        //when
        refreshTokenService.verifyExpiration(refreshToken);

        //then
        verify(refreshTokenRepository, times(0)).deleteByToken(anyString());
    }
}
