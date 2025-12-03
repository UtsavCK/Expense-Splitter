package com.example.backend.application.mapper;

import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.domain.model.user.User;
import com.example.backend.web.dto.user.UserUpdateRequest;

public final class UserMapper {
  private UserMapper() {}

  public static User toDomain(UserRequestDto dto) {
    return User.builder()
            .name(dto.name())
            .email(dto.email())
            .passwordHash(dto.password())
            .build();
  }

  public static UserResponseDto toDto(User user) {
    return new UserResponseDto(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt()
    );
  }

  public static void updateDomain(User user, UserUpdateRequest dto) {
    if (dto.name() != null) user.setName(dto.name());
    if (dto.email() != null) user.setEmail(dto.email());
    if (dto.currentPassword() != null) user.setPasswordHash(dto.currentPassword());
    if (dto.newPassword() != null) user.setPasswordHash(dto.newPassword());
  }
}
