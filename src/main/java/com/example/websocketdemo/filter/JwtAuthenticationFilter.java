package com.example.websocketdemo.filter;

import com.example.websocketdemo.authentication.RefreshAuthToken;
import com.example.websocketdemo.model.RefreshToken;
import com.example.websocketdemo.provider.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

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

        //login -> chatRoomList redirect시, refreshToken으로 접근가능하도록 하기 위함.
        if (((HttpServletRequest) request).getRequestURI().equals("/chatRoomList")) {
            Cookie[] cookies = ((HttpServletRequest) request).getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refreshToken")) {
                        RefreshToken refreshToken = refreshTokenService.findByToken(cookie.getValue());
                        if (refreshToken != null && refreshTokenService.verifyExpiration(refreshToken)) {

                            RefreshAuthToken refreshAuthToken = new RefreshAuthToken();

                            refreshAuthToken.setAuthenticated(true);
                            SecurityContextHolder.getContext().setAuthentication(refreshAuthToken);


                            break;
                        }
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
