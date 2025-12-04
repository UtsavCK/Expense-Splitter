package com.example.backend.web.controller;

import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

  @GetMapping("/current-user")
  public ResponseEntity<?> getCurrentUser(@CurrentUser Long userId) {
    return ResponseEntity.ok(Map.of("userId", userId));
  }

  @GetMapping("/auth-info")
  public ResponseEntity<?> getAuthInfo() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return ResponseEntity.ok(Map.of(
            "authenticated", auth != null && auth.isAuthenticated(),
            "principal", auth != null ? auth.getPrincipal().toString() : "null",
            "principalClass", auth != null ? auth.getPrincipal().getClass().getSimpleName() : "null"
    ));
  }
}
