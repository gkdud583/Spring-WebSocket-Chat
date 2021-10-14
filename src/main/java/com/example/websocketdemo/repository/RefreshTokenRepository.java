package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.RefreshTokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshTokenInfo, Long> {
    Optional<RefreshTokenInfo> findByToken(String token);
    void deleteByToken(String token);
}
