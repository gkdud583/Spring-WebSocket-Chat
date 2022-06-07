package com.example.websocketdemo.model;

import java.time.LocalDateTime;

public class tokenResponse {
    private String accessToken;

    private LocalDateTime expiryDate;

    public tokenResponse(String accessToken, LocalDateTime expiryDate) {
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
