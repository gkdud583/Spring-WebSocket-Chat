package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.RefreshToken;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.entity.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void findByTokenSuccess() {
        //given
        String token = "refreshToken";
        RefreshToken refreshToken = new RefreshToken(token);
        given(refreshTokenRepository.findByToken(anyString()))
                .willReturn(Optional.of(refreshToken));

        //when
        RefreshToken foundRefreshToken = refreshTokenService.findByToken(token);

        //then
        verify(refreshTokenRepository).findByToken(any());
        assertThat(foundRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    void findByTokenWithNonexistentToken() {
        //given
        String token = "refreshToken";
        given(refreshTokenRepository.findByToken(anyString()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() ->
                refreshTokenService.findByToken(token))
                .isInstanceOf(CustomException.class)
                .hasMessage("refresh token 을 찾을 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void findByTokenWithTokenIsNullOrBlack(String token) {
        //given
        //when, then
        assertThatThrownBy(() ->
                refreshTokenService.findByToken(token))
                .isInstanceOf(CustomException.class)
                .hasMessage("유효하지 않은 refresh token 입니다.");
    }
}
