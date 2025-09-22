package com.sku_sku.MatchPrediction.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class SignupJwtService {

    private final SecretKey secretKey; // JWT 서명에 사용되는 비밀 키 // 생성한 비밀 키의 타입이 SecretKey 타입

    private static final long expirationTime = 1000 * 10 * 60; // 밀리초 단위 // JWT 만료 시간: 1시간

    // JWT 서명에 사용되는 비밀 키 생성
    public SignupJwtService(@Value("${jwt.base64Secret}") String base64Secret) { // @Value을 통해 application.yml에서 값 주입
        this.secretKey = Keys.hmacShaKeyFor(base64Secret.getBytes());
    }

    // 이메일 기반 임시 토큰 생성
    public String createSignupJwt(String email) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claims()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationTime)))
                .and()
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getEmailFromSignupJwt(String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
