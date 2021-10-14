package com.example.websocketdemo.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
public class RefreshTokenInfo {

    @Id
    @Column
    private String email;

    @Column
    private String token;

    @Column
    private LocalDateTime expiryDate;

    @Builder
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
}

