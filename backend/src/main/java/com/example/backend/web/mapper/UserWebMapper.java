package com.example.backend.web.mapper;

import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.dto.user.UserUpdateDto;
import com.example.backend.web.dto.user.UserCreateRequest;
import com.example.backend.web.dto.user.UserResponse;
import com.example.backend.web.dto.user.UserUpdateRequest;

public class UserWebMapper {

  public static UserRequestDto toCreateApp(UserCreateRequest req) {
    return new UserRequestDto(
            req.name(),
            req.email(),
            req.password()
    );
  }

  public static UserResponse toWeb(UserResponseDto dto) {
    return new UserResponse(
            dto.userId(),
            dto.name(),
            dto.email(),
            dto.createdAt().toString()
    );
  }

  public static UserUpdateDto toUpdateApp(UserUpdateRequest req) {
    return new UserUpdateDto(
            req.name(),
            req.email(),
            req.currentPassword(),
            req.newPassword()
    );
  }
}
