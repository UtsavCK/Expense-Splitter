package com.example.backend.infrastructure.persistence.mapper;

import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.infrastructure.persistence.entity.GroupMemberEntity;

public class GroupMemberEntityMapper {
  private GroupMemberEntityMapper() {}

  public static GroupMemberEntity toEntity(GroupMember gm) {
    return GroupMemberEntity.builder()
            .groupMemberId(gm.getGroupMemberId())
            .group(
                    gm.getGroup() != null
                            ? GroupEntityMapper.toEntity(gm.getGroup())
                            : null
            )
            .user(
                    gm.getUser() != null
                            ? UserEntityMapper.toEntity(gm.getUser())
                            : null
            )
            .joinedAt(gm.getJoinedAt())
            .build();
  }

  public static GroupMember toDomain(GroupMemberEntity gme) {
    return GroupMember.builder()
            .groupMemberId(gme.getGroupMemberId())
            .group(
                    gme.getGroup() != null
                            ? GroupEntityMapper.toDomain(gme.getGroup())
                            : null
            )
            .user(
                    gme.getUser() != null
                            ? UserEntityMapper.toDomain(gme.getUser())
                            : null
            )
            .joinedAt(gme.getJoinedAt())
            .build();
  }
}
