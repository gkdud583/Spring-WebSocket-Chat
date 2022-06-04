package com.example.websocketdemo.controller;

import com.example.websocketdemo.entity.UserInfo;
import com.example.websocketdemo.model.RefreshToken;
import com.example.websocketdemo.model.User;
import com.example.websocketdemo.provider.JwtTokenProvider;
import com.example.websocketdemo.repository.RefreshTokenRepository;
import com.example.websocketdemo.service.RefreshTokenService;
import com.example.websocketdemo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private MockMvc mvc;

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserController userController;


    private ObjectMapper mapper = new ObjectMapper();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private MockHttpServletRequest req;
    private MockHttpServletResponse res;

    @BeforeEach
    void setup() {
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        thymeleafViewResolver.setTemplateEngine(new SpringTemplateEngine());
        //MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(thymeleafViewResolver)
                .build();
    }

    @Test
    void signupSuccess() throws Exception {
        //given
        given(userService.isExistEmail(anyString()))
                .willReturn(false);
        //when
        ResultActions ra = mvc.perform(
                post("/signup")
                        .param("email", "test@naver.com")
                        .param("password", "zns9dyek951956"));


        //then
        ra.andExpect(status().isOk());


    }

    @Test
    void signupFailByDuplicatedEmail() throws Exception {
        //given
        given(userService.isExistEmail(anyString()))
                .willReturn(true);
        //when
        ResultActions ra = mvc.perform(post("/signup")
                .param("email", "test@naver.com")
                .param("password", "zns9dyek951956"));


        //then
        ra.andExpect(status().isBadRequest());

    }

    @Test
    void loginSuccess() throws Exception {
        //given

        User user = new User("test@naver.com", "zns9dyek951956");
        given(userService.loadUserByUsername(anyString()))
                .willReturn(
                        UserInfo.builder()
                                .email(user.getEmail())
                                .auth(user.getAuth())
                                .password(encoder.encode(user.getPassword())).build()
                );
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));

        given(jwtTokenProvider.createToken("test@naver.com", roles))
                .willReturn("test");
        given(jwtTokenProvider.getTokenExpiration("test"))
                .willReturn(new Date());


        given(refreshTokenService.createRefreshToken(any(RefreshToken.class)))
                .willReturn(anyString());


        //when
        ResultActions ra = mvc.perform(post("/login")
                .requestAttr("HttpServletRequest", req)
                .requestAttr("HttpServletResponse", res)
                .param("email", user.getEmail())
                .param("password", user.getPassword()));


        //then
        MvcResult mvcResult = ra.andExpect(status().isOk()).andReturn();
        assertThat(mvcResult.getResponse().getCookie("refreshToken")).isNotNull();
        assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();

    }

    @Test
    void loginFail() throws Exception {
        //given

        User user = new User("test@naver.com", "zns9dyek951956");

        given(userService.loadUserByUsername(anyString()))
                .willReturn(
                        UserInfo.builder()
                                .email("test@naver.com")
                                .auth("ROLE_USER")
                                .password(encoder.encode("zns9dyek951956@")).build()
                );


        //when
        ResultActions ra = mvc.perform(post("/login")
                .requestAttr("HttpServletRequest", req)
                .requestAttr("HttpServletResponse", res)
                .param("email", user.getEmail())
                .param("password", user.getPassword()));


        //then
        ra.andExpect(status().isUnauthorized());

    }

    @Test
    void loginPageAfterLogin() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(RefreshToken.create("test@naver.com"));
        given(refreshTokenService.verifyExpiration(any()))
                .willReturn(true);

        //when
        ResultActions ra = mvc.perform(
                get("/login")
                        .cookie(new Cookie("refreshToken", "test")));

        //then
        MvcResult mvcResult = ra.andExpect(status().is3xxRedirection()).andReturn();
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo("/chatRoomList");


    }

    @Test
    void loginPageAfterRefreshTokenExpires() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(RefreshToken.create("test@naver.com"));
        given(refreshTokenService.verifyExpiration(any()))
                .willReturn(false);

        //when
        ResultActions ra = mvc.perform(
                get("/login")
                        .cookie(new Cookie("refreshToken", "test")));

        //then
        ra.andExpect(status().isOk());
        ra.andExpect(view().name("login"));


    }

    @Test
    void loginPageAfterRefreshTokenDeleted() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(null);

        //when
        ResultActions ra = mvc.perform(
                get("/login")
                        .cookie(new Cookie("refreshToken", "test")));

        //then
        ra.andExpect(status().isOk());
        ra.andExpect(view().name("login"));


    }

    @Test
    void loginPageBeforeLogin() throws Exception {
        //given
        given(refreshTokenService.findByToken(null))
                .willReturn(null);

        //when
        ResultActions ra = mvc.perform(
                get("/login"));


        //then
        ra.andExpect(status().isOk());
        ra.andExpect(view().name("login"));


    }

    @Test
    void logoutAfterRefreshToken() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(RefreshToken.create("test@naver.com"));

        //when
        ResultActions ra = mvc.perform(
                get("/logout")
                        .cookie(new Cookie("refreshToken", "test")));

        //then
        ra.andExpect(status().is3xxRedirection());


    }

    @Test
    void logoutWithRefreshToken() throws Exception {
        //given
        given(refreshTokenService.findByToken(any()))
                .willReturn(any(RefreshToken.class));

        //when
        ResultActions ra = mvc.perform(
                get("/logout")
                        .cookie(new Cookie("refreshToken", "test")));

        //then
        ra.andExpect(status().is3xxRedirection());


    }

    @Test
    void logoutWithNoRefreshToken() throws Exception {
        //given
        given(refreshTokenService.findByToken(null))
                .willReturn(null);

        //when
        ResultActions ra = mvc.perform(
                get("/logout"));


        //then
        ra.andExpect(status().is3xxRedirection());


    }

    @Test
    void refreshTokenSuccess() throws Exception{


        given(refreshTokenService.findByToken(anyString()))
                .willReturn(RefreshToken.create("test@naver.com"));
        given(refreshTokenService.verifyExpiration(any()))
                .willReturn(true);
        given(userService.loadUserByUsername(anyString()))
                .willReturn(
                        UserInfo.builder()
                                .email("test@naver.com")
                                .auth("ROLE_USER")
                                .password(encoder.encode("zns9dyek951956@")).build()
                );
        given(jwtTokenProvider.getTokenExpiration(any()))
                .willReturn(any());


        //when
        ResultActions ra = mvc.perform(
                get("/refreshToken")
                        .cookie(new Cookie("refreshToken", "test")));
        //then
        MvcResult mvcResult = ra.andExpect(status().isOk()).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isNotEmpty();


    }

    @Test
    void refreshTokenAfterRefreshTokenExpires() throws Exception {
        //given

        given(refreshTokenService.findByToken(anyString()))
                .willReturn(RefreshToken.create("test@naver.com"));
        given(refreshTokenService.verifyExpiration(any()))
                .willReturn(false);

        //when
        ResultActions ra = mvc.perform(
                get("/refreshToken")
                        .cookie(new Cookie("refreshToken", "test")));
        //then
        MvcResult mvcResult = ra.andExpect(status().isUnauthorized()).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();


    }

    @Test
    void refreshTokenAfterRefreshTokenDeleted() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(null);

        //when
        ResultActions ra = mvc.perform(
                get("/refreshToken")
                        .cookie(new Cookie("refreshToken", "test")));
        //then
        MvcResult mvcResult = ra.andExpect(status().isUnauthorized()).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();

    }

    @Test
    void refreshTokenWithNoRefreshToken() throws Exception {
        //given
        doReturn(null)
                .when(refreshTokenService).findByToken(null);

        //when
        ResultActions ra = mvc.perform(
                get("/refreshToken"));

        //then
        MvcResult mvcResult = ra.andExpect(status().isUnauthorized()).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEmpty();

    }
}

