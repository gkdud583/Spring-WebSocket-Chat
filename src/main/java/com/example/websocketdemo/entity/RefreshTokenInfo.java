package com.example.websocketdemo.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class RefreshTokenInfo {

    @Id
    @Column
    private String email;

    @Column
    private String token;

    @Column
    private LocalDateTime expiryDate;


    protected RefreshTokenInfo() {
    }

    public RefreshTokenInfo(String email, String token, LocalDateTime expiryDate){
        this.email = email;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public RefreshTokenInfo(String email, LocalDateTime expiryDate) {
        this.email = email;
        this.token = UUID.randomUUID().toString();
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

