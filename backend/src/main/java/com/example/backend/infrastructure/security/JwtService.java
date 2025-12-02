package com.example.backend.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final long expirationMs;

  public JwtService(
          @Value("${jwt.secret}") String secret,
          @Value("${jwt.expiration}") long expirationMs
  ) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMs = expirationMs;
  }

  public String generateToken(Long userId, String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey)
            .compact();
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    return Long.parseLong(claims.getSubject());
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
              .setSigningKey(secretKey)
              .build()
              .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
