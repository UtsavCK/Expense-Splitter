package com.example.backend.application.usecase;

import com.example.backend.application.dto.auth.LoginRequestDto;
import com.example.backend.application.dto.auth.LoginResponseDto;

public interface AuthUseCase {
  LoginResponseDto login(LoginRequestDto dto);
}
