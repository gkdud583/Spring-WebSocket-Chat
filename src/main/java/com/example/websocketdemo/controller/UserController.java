package com.example.websocketdemo.controller;

import com.example.websocketdemo.entity.RefreshToken;
import com.example.websocketdemo.entity.User;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.service.dto.TokenResponse;
import com.example.websocketdemo.service.dto.UserSaveRequest;
import com.example.websocketdemo.jwt.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import com.example.websocketdemo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import static com.example.websocketdemo.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@RequestMapping("api/v1/users")
@Controller
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void signup(@RequestBody @Valid UserSaveRequest request) {
        userService.save(request.getEmail(), request.getPassword());
    }

    //로그인 성공 후 로그인 페이지 접근 막기
    @GetMapping("/login")
    public String showLoginForm(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (!refreshTokenService.existsByToken(refreshToken)) {
            return "login";
        }
        return "redirect:/chatRoomList";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity login(HttpServletResponse response, @RequestBody @Valid UserSaveRequest request) {
        userService.login(request.getEmail(), request.getPassword());

        User user = userService.findByEmail(request.getEmail());

        //accessToken
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());

        //accessToken Expiration
        LocalDateTime expiration = jwtTokenProvider.getTokenExpiration(accessToken);

        //refreshToken
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        refreshTokenService.save(refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        // optional properties
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");


        response.addCookie(refreshTokenCookie);

        TokenResponse responseToken = new TokenResponse(accessToken, expiration);
        return new ResponseEntity<>(responseToken, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
        return "redirect:/login";
    }


    @PostMapping("/refreshToken")
    @ResponseBody
    public ResponseEntity refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {

        RefreshToken foundRefreshToken = refreshTokenService.findByToken(refreshToken);

        if (!jwtTokenProvider.validateToken(foundRefreshToken.getToken())) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
        String accessToken = jwtTokenProvider.createAccessToken(jwtTokenProvider.getId(foundRefreshToken.getToken()));
        LocalDateTime expiration = jwtTokenProvider.getTokenExpiration(accessToken);
        TokenResponse responseToken = new TokenResponse(accessToken, expiration);
        return new ResponseEntity<>(responseToken, HttpStatus.OK);
    }


    //테스트용 데이터 추가
    @PostConstruct
    public void init() {
        userService.save("test@naver.com", "zns9dyek951956");
        userService.save("test2@naver.com", "zns9dyek951956");
    }
}
