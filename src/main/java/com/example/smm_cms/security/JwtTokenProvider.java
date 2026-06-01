package com.example.smm_cms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String SECRET =
            "my-secret-key-my-secret-key";

    public String generateToken(String username) {

        Date now = new Date();

        Date expired =
                new Date(now.getTime() + 86400000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expired)
                .signWith(
                        Keys.hmacShaKeyFor(
                                SECRET.getBytes()
                        )
                )
                .compact();
    }

    public String getUsername(String token) {

        return Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(
                                SECRET.getBytes()
                        )
                )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}