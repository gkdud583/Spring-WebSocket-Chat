package com.example.websocketdemo.service;

import com.example.websocketdemo.entity.RefreshTokenInfo;
import com.example.websocketdemo.model.RefreshToken;
import com.example.websocketdemo.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class RefreshTokenService {


    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.save(RefreshTokenInfo.builder()
                .email(refreshToken.getEmail())
                .token(refreshToken.getToken())
                .expiryDate(refreshToken.getExpiryDate()).build());

        return refreshToken.getToken();
    }


    public RefreshToken findByToken(String token){
        Optional<RefreshTokenInfo> findTokenInfo = refreshTokenRepository.findByToken(token);
        if(findTokenInfo.isPresent()){

            RefreshToken findToken = new RefreshToken(findTokenInfo.get().getEmail());
            findToken.setToken(findTokenInfo.get().getToken());
            findToken.setExpiryDate(findTokenInfo.get().getExpiryDate());

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
