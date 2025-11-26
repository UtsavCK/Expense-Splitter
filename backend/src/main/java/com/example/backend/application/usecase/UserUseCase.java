package com.example.backend.application.usecase;

import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;

import java.util.Optional;

public interface UserUseCase {
  UserResponseDto createUser(UserRequestDto dto);
  Optional<UserResponseDto> getUserById(Long id);
}
