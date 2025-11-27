package com.example.backend.application.usecase;

import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.web.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserUseCase {
  UserResponseDto createUser(UserRequestDto dto);
  Optional<UserResponseDto> getUserById(Long id);
  List<UserResponseDto> getAllUsers();
  UserResponseDto updateUser(Long id, UserUpdateRequest dto);
  void deleteUser(Long id);
}
