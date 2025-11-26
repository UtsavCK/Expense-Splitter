package com.example.backend.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24h

  public String generateToken(Long userId, String email) {
    return Jwts.builder()
            .subject(email)
            .claim("userId", userId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(key)
            .compact();
  }

  public Claims validate(String token) {
    try {
      return Jwts.parser()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (JwtException | IllegalArgumentException e) {
      System.out.println("JWT validation failed: " + e.getMessage());
      return null;
    }
  }
}
