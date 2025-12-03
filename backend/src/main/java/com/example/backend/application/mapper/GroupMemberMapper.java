package com.example.backend.application.mapper;

import com.example.backend.application.dto.group.GroupMemberResponseDto;
import com.example.backend.domain.model.group.GroupMember;

public class GroupMemberMapper {

  public static GroupMemberResponseDto toDto(GroupMember gm) {
    return new GroupMemberResponseDto(
            gm.getGroupMemberId(),
            gm.getGroup().getGroupId(),
            gm.getUser().getUserId(),
            gm.getUser().getName(),
            gm.getUser().getEmail(),
            gm.getJoinedAt()
    );
  }
}
