package com.example.websocketdemo.filter;

import com.example.websocketdemo.entity.UserInfo;
import com.example.websocketdemo.model.RefreshToken;
import com.example.websocketdemo.provider.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import com.example.websocketdemo.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
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



    @Test
    void chatRoomListPageBeforeLogin() throws  Exception{
        //given
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(null);


        //when
        ResultActions ra = mvc.perform(get("/chatRoomList"));

        //then
        MvcResult mvcResult = ra.andExpect(status().is3xxRedirection()).andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo("/login");

    }

    @Test
    void chatRoomListPageAfterLogin() throws  Exception{
        //given

        String accessToken = "test";
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(anyString());
        given(jwtTokenProvider.validateToken(accessToken))
                .willReturn(true);
        given(jwtTokenProvider.getAuthentication(any()))
                .willReturn(any(Authentication.class));

        //when
        ResultActions ra = mvc.perform(get("/chatRoomList")
                .header("Authorization", accessToken));

        //then
        MvcResult mvcResult = ra.andExpect(status().is3xxRedirection()).andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo("/login");

    }
    @Test
    void chatRoomListPageWithOnlyRefreshToken() throws  Exception{
        //given

        String refreshToken = "test";
        String email = "test@naver.com";
        given(jwtTokenProvider.resolveToken(any()))
                .willReturn(null);
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(any(RefreshToken.class));
        given(refreshTokenService.verifyExpiration(RefreshToken.create(email)))
                .willReturn(true);

        //when
        ResultActions ra = mvc.perform(get("/chatRoomList")
                .cookie(new Cookie("refreshToken", refreshToken)));

        //then
        MvcResult mvcResult = ra.andExpect(status().is3xxRedirection()).andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo("/login");

    }





}
