package com.example.backend.application.usecase;

import com.example.backend.application.dto.auth.AuthResponseDto;
import com.example.backend.application.dto.auth.LoginRequestDto;
import com.example.backend.application.dto.auth.RegisterRequestDto;

public interface AuthUseCase {
  AuthResponseDto login(LoginRequestDto request);
  AuthResponseDto register(RegisterRequestDto request);
}
