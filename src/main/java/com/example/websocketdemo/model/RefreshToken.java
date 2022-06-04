package com.example.websocketdemo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {

    //3시간 동안 유지
    private static long tokenValidTime = 180;

    private String email;
    private String token;
    private LocalDateTime expiryDate;


    public RefreshToken(String email, String token) {
        this.email = email;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusMinutes(tokenValidTime);
    }

    public RefreshToken(String email, String token, LocalDateTime expiryDate) {
        this.email = email;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
