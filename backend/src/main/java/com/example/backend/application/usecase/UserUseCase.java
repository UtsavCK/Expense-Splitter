package com.example.backend.application.usecase;

import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.web.dto.user.UserUpdateRequest;

import java.util.Optional;

public interface UserUseCase {
  Optional<UserResponseDto> getUserById(Long id);
  Optional<UserResponseDto> getUserByEmail(String email);
  UserResponseDto updateUser(Long id, UserUpdateRequest dto);
  void deleteUser(Long id);
}
