package com.example.websocketdemo.controller;

import com.example.websocketdemo.entity.RefreshToken;
import com.example.websocketdemo.entity.User;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.service.dto.UserSaveRequest;
import com.example.websocketdemo.jwt.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import com.example.websocketdemo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import static com.example.websocketdemo.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                                classes = WebSecurityConfigurerAdapter.class),}
)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private MockHttpServletRequest req;
    private MockHttpServletResponse res;

    private User user = new User("test@naver.com", "test123456789");
    private UserSaveRequest request = new UserSaveRequest("test@naver.com", "test123456789");
    private final String BASE_URL = "/api/v1/users";
    private final String SIGNUP_URL = BASE_URL + "/signup";
    private final String LOGIN_URL = BASE_URL + "/login";
    private final String LOGOUT_URL = BASE_URL + "/logout";
    private final String REFRESH_TOKEN_URL = BASE_URL + "/refreshToken";

    @BeforeEach
    void setup() {
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
    }

    @Test
    @WithMockUser
    void signupSuccess() throws Exception {
        //given
        //when, then
        mvc.perform(post(SIGNUP_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf())
           .contentType(APPLICATION_JSON)
           .content(toJson(request)))
           .andExpect(status().isOk());

        //then
        verify(userService).save(anyString(), anyString());
    }

    @Test
    @WithMockUser
    void signupFailByDuplicatedEmail() throws Exception {
        //given
        willThrow(new CustomException(DUPLICATE_ACCOUNT))
                .given(userService).save(anyString(), anyString());

        //when, then
        mvc.perform(post(SIGNUP_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf())
           .contentType(APPLICATION_JSON)
           .content(toJson(request)))
           .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void loginSuccess() throws Exception {
        //given
        given(userService.findByEmail(anyString()))
                .willReturn(user);
        given(jwtTokenProvider.createAccessToken(anyString()))
                .willReturn("accessToken");
        given(jwtTokenProvider.getTokenExpiration(anyString()))
                .willReturn(LocalDateTime.now());
        given(jwtTokenProvider.createRefreshToken(anyString()))
                .willReturn("refreshToken");

        //when
        MvcResult result = mvc.perform(post(LOGIN_URL)
                              .with(SecurityMockMvcRequestPostProcessors.csrf())
                              .requestAttr("HttpServletRequest", req)
                              .requestAttr("HttpServletResponse", res)
                              .contentType(APPLICATION_JSON)
                              .content(toJson(request)))
                              .andReturn();

        //then
        assertThat(result.getResponse().getCookie("refreshToken")).isNotNull();
        assertThat(result.getResponse().getContentAsString()).isNotNull();
    }

    @Test
    @WithMockUser
    void loginFail() throws Exception {
        //given
        willThrow(new CustomException(UNAUTHENTICATED_USER))
                .given(userService).login(anyString(), anyString());

        //when, then
        mvc.perform(post(LOGIN_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf())
           .requestAttr("HttpServletRequest", req)
           .requestAttr("HttpServletResponse", res)
           .contentType(APPLICATION_JSON)
           .content(toJson(request)))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void loginPageAfterLogin() throws Exception {
        //given
        String redirectLocation = "/chatRoomList";
        given(refreshTokenService.existsByToken(anyString()))
                .willReturn(false);

        //when
        MvcResult mvcResult = mvc.perform(get(LOGIN_URL)
                                 .with(SecurityMockMvcRequestPostProcessors.csrf())
                                 .cookie(new Cookie("refreshToken", "test")))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(FOUND.value());
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo(redirectLocation);
    }

    @Test
    @WithMockUser
    void loginPageBeforeLogin() throws Exception {
        //given
        String viewName = "redirect:/chatRoomList";
        given(refreshTokenService.findByToken(anyString()))
                .willThrow(new CustomException(NOT_FOUND_REFRESH_TOKEN));

        //when
        MvcResult mvcResult = mvc.perform(get(LOGIN_URL)
                                 .with(SecurityMockMvcRequestPostProcessors.csrf())
                                 .cookie(new Cookie("refreshToken", "test")))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(FOUND.value());
        assertThat(mvcResult.getModelAndView().getViewName()).isEqualTo(viewName);
    }

    @Test
    @WithMockUser
    void logoutSuccess() throws Exception {
        //given
        String redirectLocation = "/login";

        //when
        MvcResult mvcResult = mvc.perform(get(LOGOUT_URL)
                                 .with(SecurityMockMvcRequestPostProcessors.csrf())
                                 .cookie(new Cookie("refreshToken", "test")))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(FOUND.value());
        assertThat(mvcResult.getResponse().getRedirectedUrl()).isEqualTo(redirectLocation);
    }

    @Test
    @WithMockUser
    void refreshTokenSuccess() throws Exception {
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(new RefreshToken("refreshToken"));
        given(jwtTokenProvider.validateToken(anyString()))
                .willReturn(true);
        given(jwtTokenProvider.getId(anyString()))
                .willReturn(user.getId());
        given(jwtTokenProvider.createAccessToken(anyString()))
                .willReturn("accessToken");
        given(jwtTokenProvider.getTokenExpiration(anyString()))
                .willReturn(LocalDateTime.now());

        //when
        MvcResult mvcResult = mvc.perform(post(REFRESH_TOKEN_URL)
                                 .with(SecurityMockMvcRequestPostProcessors.csrf())
                                 .cookie(new Cookie("refreshToken", "test")))
                                 .andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(OK.value());
        assertThat(mvcResult.getResponse().getContentAsString()).isNotEmpty();
    }

    @Test
    @WithMockUser
    void refreshTokenAfterRefreshTokenExpires() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willReturn(new RefreshToken("refreshToken"));
        given(jwtTokenProvider.validateToken(anyString()))
                .willReturn(false);

        //when, then
        mvc.perform(post(REFRESH_TOKEN_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf())
           .cookie(new Cookie("refreshToken", "test")))
           .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void refreshTokenAfterRefreshTokenDeleted() throws Exception {
        //given
        given(refreshTokenService.findByToken(anyString()))
                .willThrow(new CustomException(INVALID_REFRESH_TOKEN));

        //when, then
        mvc.perform(post(REFRESH_TOKEN_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf())
           .cookie(new Cookie("refreshToken", "test")))
           .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void refreshTokenWithNoRefreshToken() throws Exception {
        //given
        //when, then
        mvc.perform(post(REFRESH_TOKEN_URL)
           .with(SecurityMockMvcRequestPostProcessors.csrf()))
           .andExpect(status().isBadRequest());
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

