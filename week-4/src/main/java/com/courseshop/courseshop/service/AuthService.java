package com.courseshop.courseshop.service;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.model.LoginRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class AuthService {

    public static final long TOKEN_LIFETIME_SECONDS = 3600;

    private final String demoUsername;
    private final String demoPassword;
    private final SecretKey jwtSigningKey = Jwts.SIG.HS256.key().build();

    public AuthService(
            @Value("${app.auth.demo-username:demo}") String demoUsername,
            @Value("${app.auth.demo-password:demo123}") String demoPassword) {
        this.demoUsername = demoUsername;
        this.demoPassword = demoPassword;
    }

    public boolean authenticate(LoginRequest request) {
        return request != null
                && demoUsername.equals(request.getUsername())
                && demoPassword.equals(request.getPassword());
    }

    public String createAccessToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(TOKEN_LIFETIME_SECONDS)))
                .signWith(jwtSigningKey)
                .compact();
    }

    public String getUsernameFromValidToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSigningKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean isValidToken(String token) {
        try {
            return getUsernameFromValidToken(token) != null;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
