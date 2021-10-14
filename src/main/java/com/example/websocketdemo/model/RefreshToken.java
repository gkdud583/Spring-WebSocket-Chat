package com.example.websocketdemo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Setter


public class RefreshToken {

    //3시간 동안 유지
    private static long tokenValidTime = 180;

    private String email;
    private String token;
    private LocalDateTime expiryDate;




    public RefreshToken(String email) {
        this.email = email;

    }
    public static RefreshToken create(String email){
        RefreshToken refreshToken = new RefreshToken(email);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(tokenValidTime));

        return refreshToken;
    }
}
