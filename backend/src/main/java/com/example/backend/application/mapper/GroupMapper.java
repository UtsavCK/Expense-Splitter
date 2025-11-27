package com.example.backend.application.mapper;

import com.example.backend.application.dto.group.GroupRequestDto;
import com.example.backend.application.dto.group.GroupResponseDto;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;

import java.time.LocalDateTime;

public class GroupMapper {
  private GroupMapper() {}

  public static Group toDomain(GroupRequestDto dto, User creator) {
    return Group.builder()
            .name(dto.name())
            .createdBy(creator)
            .createdAt(LocalDateTime.now())
            .build();
  }

  public static GroupResponseDto toDto(Group group) {
    return new GroupResponseDto(
            group.getGroupId(),
            group.getName(),
            group.getCreatedBy().getUserId(),
            group.getCreatedAt()
    );
  }
}
