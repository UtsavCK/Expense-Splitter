package com.example.backend.web.mapper;


import com.example.backend.application.dto.auth.LoginRequestDto;
import com.example.backend.application.dto.auth.LoginResponseDto;
import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.web.dto.auth.LoginRequest;
import com.example.backend.web.dto.auth.LoginResponse;
import com.example.backend.web.dto.auth.RegisterRequest;
import com.example.backend.web.dto.auth.RegisterResponse;

public class AuthWebMapper {

  public static UserRequestDto toApplication(RegisterRequest req) {
    return new UserRequestDto(req.name(), req.email(), req.password());
  }

  public static RegisterResponse toWeb(UserResponseDto dto) {
    return new RegisterResponse(dto.userId(), dto.name(), dto.email());
  }

  public static LoginRequestDto toApplication(LoginRequest req) {
    return new LoginRequestDto(req.email(), req.password());
  }

  public static LoginResponse toWeb(LoginResponseDto dto) {
    return new LoginResponse(dto.token(), dto.userId(), dto.email(), dto.name());
  }
}
