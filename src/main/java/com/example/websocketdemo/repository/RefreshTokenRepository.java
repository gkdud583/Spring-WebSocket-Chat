package com.example.websocketdemo.repository;

import com.example.websocketdemo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByToken(String token);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);
}
