package com.example.websocketdemo.filter;

import com.example.websocketdemo.authentication.JwtAuthenticationToken;
import com.example.websocketdemo.entity.RefreshToken;
import com.example.websocketdemo.provider.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private static final String REFRESH_TOKEN_AUTHENTICATION_URI = "/chatRoomList";
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //헤더에서 JWT를 받아옴
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        //유효한 토큰인지 확인함
        if (token != null && jwtTokenProvider.validateToken(token)) {
            //토큰이 유효하면 토큰으로부터 유저정보를 받아옴.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            //SecurityContext에 Authentication객체를 저장함.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // login -> chatRoomList redirect시, refreshToken으로 접근가능하도록 하기 위함.
        else {
            if (((HttpServletRequest) request).getRequestURI().equals(REFRESH_TOKEN_AUTHENTICATION_URI)) {
                Cookie[] cookies = ((HttpServletRequest) request).getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("refreshToken")) {
                            RefreshToken refreshToken = refreshTokenService.findByToken(cookie.getValue());
                            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken.getToken())) {
                                Authentication authentication = new JwtAuthenticationToken(null, null);
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                break;
                            }
                        }
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
