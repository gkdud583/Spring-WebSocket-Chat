package com.example.websocketdemo.config;


import com.example.websocketdemo.exception.AuthEntryPointJwt;
import com.example.websocketdemo.filter.JwtAuthenticationFilter;
import com.example.websocketdemo.provider.JwtTokenProvider;
import com.example.websocketdemo.service.RefreshTokenService;
import com.example.websocketdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, "/signup");

        //Spring Security가 기본 제공하는 formLogin, logout사용 하지 않도록 하기 위해 ignoring에 추가
        web.ignoring().antMatchers("/ws/**","/", "/join", "/login", "/logout", "/refreshToken", "/js/**", "/css/**", "/error");
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
            .addFilterAfter(new JwtAuthenticationFilter(refreshTokenService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

    }



    @Bean
    public AuthenticationEntryPoint unauthorizedHandler(){return new AuthEntryPointJwt();}

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception { // 9
        auth.userDetailsService(userService)
                // 해당 서비스(userService)에서는 UserDetailsService를 implements해서
                // loadUserByUsername() 구현해야함 (서비스 참고)
                .passwordEncoder(new BCryptPasswordEncoder());
    }



}