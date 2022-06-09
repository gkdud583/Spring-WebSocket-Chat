package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.RefreshToken;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.entity.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.websocketdemo.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.example.websocketdemo.exception.ErrorCode.NOT_FOUND_REFRESH_TOKEN;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void save(String token) {
        validateToken(token);
        refreshTokenRepository.save(new RefreshToken(token));
    }

    public boolean existsByToken(String token) {
        validateToken(token);
        return refreshTokenRepository.existsByToken(token);
    }

    public RefreshToken findByToken(String token) {
        validateToken(token);
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new CustomException(NOT_FOUND_REFRESH_TOKEN));
    }

    public void deleteByToken(String token) {
        validateToken(token);
        refreshTokenRepository.deleteByToken(token);
    }

    private void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }
    }
}
