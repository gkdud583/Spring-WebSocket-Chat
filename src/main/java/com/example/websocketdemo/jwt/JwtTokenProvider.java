package com.example.websocketdemo.jwt;

import com.example.websocketdemo.entity.User;
import com.example.websocketdemo.exception.CustomException;
import com.example.websocketdemo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import static com.example.websocketdemo.exception.ErrorCode.INVALID_TOKEN;

@Component
public class JwtTokenProvider {
    private static final String AUTH_HEADER_NAME = "Authorization";
    @Value("${app.jwtSecret}")
    private String secretKey;

    @Value("${app.accessTokenExpirationMs}")
    private long accessTokenValidTime;

    @Value("${app.refreshTokenExpirationMs}")
    private long refreshTokenValidTime;

    private final UserService userService;

    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    //객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //JWT 토큰 생성
    public String createAccessToken(String id) {
        Claims claims = Jwts.claims().setSubject(id);
        Date now = new Date();
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(now)
                   .setExpiration(new Date((new Date()).getTime() + accessTokenValidTime))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    //JWT 토큰 생성
    public String createRefreshToken(String id) {
        Claims claims = Jwts.claims().setSubject(id);
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + refreshTokenValidTime))
                   .signWith(SignatureAlgorithm.HS256, secretKey)
                   .compact();
    }

    //토큰에서 회원정보 추출
    public String getId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        User user = userService.findById(this.getId(token));
        return new JwtAuthenticationToken(user, null);
    }

    //Request의 Header에서 token값을 가져옴. "Authorization" : "TOKEN값"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER_NAME);
    }

    //토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    //토큰의 만료일자 받기
    public LocalDateTime getTokenExpiration(String jwtToken) {
        LocalDateTime expiration = null;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            expiration = claims.getBody().getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            throw new CustomException(INVALID_TOKEN);
        }
        return expiration;
    }
}
