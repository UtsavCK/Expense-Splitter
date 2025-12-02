package com.example.backend.web.controller;

import com.example.backend.application.dto.auth.AuthResponseDto;
import com.example.backend.application.dto.auth.LoginRequestDto;
import com.example.backend.application.dto.auth.RegisterRequestDto;
import com.example.backend.application.usecase.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthUseCase authUseCase;

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
    AuthResponseDto response = authUseCase.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request) {
    AuthResponseDto response = authUseCase.register(request);
    return ResponseEntity.ok(response);
  }
}