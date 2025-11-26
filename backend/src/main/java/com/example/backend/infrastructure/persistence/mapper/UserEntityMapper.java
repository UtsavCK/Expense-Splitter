package com.example.backend.infrastructure.persistence.mapper;

import com.example.backend.domain.model.user.User;
import com.example.backend.infrastructure.persistence.entity.UserEntity;

public class UserEntityMapper {
  private UserEntityMapper() {}

  public static UserEntity toEntity(User user) {
    return UserEntity.builder()
            .userId(user.getUserId())
            .name(user.getName())
            .email(user.getEmail())
            .passwordHash(user.getPasswordHash())
            .createdAt(user.getCreatedAt())
            .build();
  }

  public static User toDomain(UserEntity ue){
    return User.builder()
            .userId(ue.getUserId())
            .name(ue.getName())
            .email(ue.getEmail())
            .passwordHash(ue.getPasswordHash())
            .createdAt(ue.getCreatedAt())
            .build();
  }
}
