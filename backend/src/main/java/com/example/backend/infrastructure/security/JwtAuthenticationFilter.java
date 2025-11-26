package com.example.backend.infrastructure.security;

import com.example.backend.infrastructure.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends GenericFilter {

  private final JwtProvider jwtProvider;

  public JwtAuthenticationFilter(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    String header = req.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      try {
        String token = header.substring(7);
        Claims claims = jwtProvider.validate(token);

        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();

        var auth = new UsernamePasswordAuthenticationToken(
                email, null, Collections.emptyList());

        auth.setDetails(userId);

        SecurityContextHolder.getContext().setAuthentication(auth);

      } catch (Exception ignored) {}
    }

    chain.doFilter(request, response);
  }
}
