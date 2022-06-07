package com.example.websocketdemo.config;

import com.example.websocketdemo.exception.AuthEntryPointJwt;
import com.example.websocketdemo.jwt.JwtAuthenticationFilter;
import com.example.websocketdemo.jwt.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import com.example.websocketdemo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public WebSecurityConfig(UserService userService, RefreshTokenService refreshTokenService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, "/api/v1/users/**");
        web.ignoring().antMatchers(HttpMethod.GET, "/api/v1/users/**");
        web.ignoring().antMatchers("/ws/**", "/", "/join", "/chatRoomList", "/js/**", "/css/**", "/error", "/h2-console/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
                csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider, refreshTokenService), UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return new AuthEntryPointJwt();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
