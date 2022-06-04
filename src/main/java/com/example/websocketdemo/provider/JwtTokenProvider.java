package com.example.websocketdemo.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${custom.jwtSecret}")
    private String secretKey;

    @Value("${custom.jwtExpirationMs}")
    private long tokenValidTime;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected  void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

    }
    //JWT 토큰 생성
    public String createToken(String userPk, Collection<? extends GrantedAuthority> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); //JWT payload에 저장되는 정보단위e
        claims.put("roles", roles); //정보는 key/value 쌍으로 저장된다.

        Date now = new Date();


        return Jwts.builder()
                .setClaims(claims) //정보저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date((new Date()).getTime() + tokenValidTime))//set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)
                //사용할 암호화 알고리즘, signature 에 들어갈 secret값 세팅
                .compact();



    }

    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails,
                "",userDetails.getAuthorities());
    }
    //토큰에서 회원정보 추출
    public String getUserPk(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //Request의 Header에서 token값을 가져옴. "Authorization" : "TOKEN값"
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    //토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken){

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }

    }

    //토큰의 만료일자 받기
    public Date getTokenExpiration(String jwtToken){
        Date expiration = null;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            expiration = claims.getBody().getExpiration();

        }catch(Exception e){
        }
        return expiration;

    }
}
