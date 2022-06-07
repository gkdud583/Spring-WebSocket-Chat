package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.User;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import java.util.UUID;
import static com.example.websocketdemo.exception.ErrorCode.DUPLICATE_ACCOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final String email = "test@naver.com";
    private final String password = "test123456789";
    private final User user = new User(email, new BCryptPasswordEncoder().encode(password));

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void saveFailWithEmailIsNullOrBlack(String emailValue) {
        //given
        //when, then
        assertThatThrownBy(() ->
                userService.save(emailValue, password))
                .isInstanceOf(CustomException.class)
                .hasMessage("유효하지 않은 email 입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void saveFailWithPasswordIsNullOrBlack(String passwordValue) {
        //given
        //when, then
        assertThatThrownBy(() ->
                userService.save(email, passwordValue))
                .isInstanceOf(CustomException.class)
                .hasMessage("유효하지 않은 password 입니다.");
    }

    @Test
    void saveWithExistentEmail() {
        //given
        given(userRepository.existsByEmail(anyString()))
                .willThrow(new CustomException(DUPLICATE_ACCOUNT));

        //when, then
        assertThatThrownBy(() ->
                userService.save(email, password))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 가입된 계정이 있습니다.");
    }

    @Test
    void findByIdSuccess() {
        //given
        String id = user.getId();
        given(userRepository.findById(anyString()))
                .willReturn(Optional.of(user));

        //when
        User foundUser = userService.findById(id);

        //then
        verify(userRepository).findById(anyString());
        assertThat(foundUser).isEqualTo(user);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void findByIdFailWithIdIsNullOrBlack(String id) {
        //given
        //when, then
        assertThatThrownBy(() ->
                userService.findById(id))
                .isInstanceOf(CustomException.class)
                .hasMessage("유효하지 않은 id 입니다.");
    }

    @Test
    void findByIdWithNonexistentId() {
        //given
        String id = UUID.randomUUID().toString();
        given(userRepository.findById(anyString()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() ->
                userService.findById(id))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }

    @Test
    void findByEmailSuccess() {
        //given
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(user));

        //when
        User foundUser = userService.findByEmail(email);

        //then
        verify(userRepository).findByEmail(anyString());
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void findByEmailWithNonexistentEmail() {
        //given
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() ->
                userService.findByEmail(email))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }

    @Test
    void loginSuccess() {
        //given
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(user));

        //when
        userService.login(email, password);

        //then
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    void loginWithIncorrectPassword() {
        //given
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(user));

        //when, then
        assertThatThrownBy(() ->
                userService.login(email, "IncorrectPassword"))
                .isInstanceOf(CustomException.class)
                .hasMessage("로그인 정보가 올바르지 않습니다.");
    }

    @Test
    void loginWithNonexistentUser() {
        //given
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() ->
                userService.login(email, password))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }
}
