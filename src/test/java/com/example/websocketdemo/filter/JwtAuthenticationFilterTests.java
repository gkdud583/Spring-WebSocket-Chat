package com.example.websocketdemo.filter;

import com.example.websocketdemo.entity.RefreshToken;
import com.example.websocketdemo.jwt.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import javax.servlet.http.Cookie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTests {
    @Autowired
    MockMvc mvc;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private final String REQUEST_URL = "/api/v1/chatRooms";
    private final String REDIRECT_URL = "/login";

    @Test
    void beforeLogin() throws Exception {
        //given
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(null);

        //when
        MvcResult mvcResult = mvc.perform(get(REQUEST_URL))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(FOUND.value());
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo(REDIRECT_URL);
    }

    @Test
    void afterLogin() throws Exception {
        //given
        String accessToken = "test";
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(anyString());
        given(jwtTokenProvider.validateToken(accessToken))
                .willReturn(true);
        given(jwtTokenProvider.getAuthentication(any()))
                .willReturn(new UsernamePasswordAuthenticationToken("test@naver.com", null, null));

        //when, then
        mvc.perform(get(REQUEST_URL)
           .header("Authorization", accessToken))
           .andExpect(status().isOk());
    }

    @Test
    void chatRoomListPageWithOnlyRefreshToken() throws Exception {
        //given
        String refreshToken = "test";
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(null);
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(new RefreshToken(refreshToken));
        given(jwtTokenProvider.validateToken(anyString()))
                .willReturn(true);

        //when, then
        mvc.perform(get("/chatRoomList")
           .cookie(new Cookie("refreshToken", refreshToken)))
           .andExpect(status().isOk());
    }

    @Test
    void otherPageWithOnlyRefreshToken() throws Exception {
        //given
        String refreshToken = "test";
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(null);
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(new RefreshToken(refreshToken));
        given(jwtTokenProvider.validateToken(anyString()))
                .willReturn(true);

        //when
        MvcResult mvcResult = mvc.perform(get("/api/v1/chatRooms")
                                 .cookie(new Cookie("refreshToken", refreshToken)))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(FOUND.value());
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo(REDIRECT_URL);
    }
}
