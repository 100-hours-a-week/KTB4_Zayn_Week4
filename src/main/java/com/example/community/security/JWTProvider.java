package com.example.community.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTProvider implements TokenProvider {
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();
    // 추후 application.properties에 고정 시크릿 키로

    @Override
    public String createAccessToken(int userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000L * 60 * 30);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "access")
                .issuedAt(now) // 토큰 발급 시간
                .expiration(expiration) // 토큰 만료 시간
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String createRefreshToken(int userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 7);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);

            return "access".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);

            return "refresh".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getUserId(String token) {
        Claims claims = parseClaims(token);
        return Integer.parseInt(claims.getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}