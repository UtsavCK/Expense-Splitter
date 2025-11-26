package com.example.backend.infrastructure.persistence.mapper;

import com.example.backend.domain.model.group.Group;
import com.example.backend.infrastructure.persistence.entity.GroupEntity;

public class GroupEntityMapper {
  private GroupEntityMapper() {}

  public static GroupEntity toEntity(Group g) {
    return GroupEntity.builder()
            .groupId(g.getGroupId())
            .name(g.getName())
            .createdBy(
                    g.getCreatedBy() != null
                            ? UserEntityMapper.toEntity(g.getCreatedBy())
                            : null
            )
            .createdAt(g.getCreatedAt())
            .build();
  }

  public static Group toDomain(GroupEntity ge) {
    return Group.builder()
            .groupId(ge.getGroupId())
            .name(ge.getName())
            .createdBy(
                    ge.getCreatedBy() != null
                            ? UserEntityMapper.toDomain(ge.getCreatedBy())
                            : null
            )
            .createdAt(ge.getCreatedAt())
            .build();
  }
}
