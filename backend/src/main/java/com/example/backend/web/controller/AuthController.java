package com.example.backend.web.controller;

import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.service.AuthApplicationService;
import com.example.backend.web.dto.auth.LoginRequest;
import com.example.backend.web.dto.auth.LoginResponse;
import com.example.backend.web.dto.auth.RegisterRequest;
import com.example.backend.web.dto.auth.RegisterResponse;
import com.example.backend.web.mapper.AuthWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthApplicationService authService;

  @PostMapping("/register")
  public RegisterResponse register(@RequestBody RegisterRequest request) {
    UserRequestDto dto = AuthWebMapper.toApplication(request);
    UserResponseDto created = authService.register(dto);
    return AuthWebMapper.toWeb(created);
  }
  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    var dto = AuthWebMapper.toApplication(request);
    var loginResponse = authService.login(dto);
    return AuthWebMapper.toWeb(loginResponse);
  }
}
