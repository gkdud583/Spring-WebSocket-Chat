package com.example.websocketdemo.service.dto;

import java.time.LocalDateTime;

public class TokenResponse {
    private String accessToken;

    private LocalDateTime expiryDate;

    public TokenResponse(String accessToken, LocalDateTime expiryDate) {
        this.accessToken = accessToken;
        this.expiryDate = expiryDate;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
