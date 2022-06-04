package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.RefreshTokenInfo;
import com.example.websocketdemo.model.RefreshToken;
import com.example.websocketdemo.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class RefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.save(new RefreshTokenInfo(refreshToken.getEmail(), refreshToken.getToken(), refreshToken.getExpiryDate()));
        return refreshToken.getToken();
    }


    public RefreshToken findByToken(String token){
        Optional<RefreshTokenInfo> findTokenInfo = refreshTokenRepository.findByToken(token);
        if(findTokenInfo.isPresent()){

            RefreshToken findToken = new RefreshToken(findTokenInfo.get().getEmail(), findTokenInfo.get().getToken(), findTokenInfo.get().getExpiryDate());
            return findToken;
        }else{
            return null;
        }
    }
    public boolean verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByToken(token.getToken());
            return false;
        }
        return true;
    }

}
