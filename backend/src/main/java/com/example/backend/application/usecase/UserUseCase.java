package com.example.backend.application.usecase;

import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.dto.user.UserSearchResultDto;
import com.example.backend.application.dto.user.UserStatsDto;
import com.example.backend.web.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserUseCase {
  Optional<UserResponseDto> getUserById(Long id);
  Optional<UserResponseDto> getUserByEmail(String email);
  UserResponseDto updateUser(Long id, UserUpdateRequest dto);
  void deleteUser(Long id);
  List<UserSearchResultDto> searchUsers(String query);
  UserStatsDto getUserStats(Long userId);
}
