package com.example.backend.web.mapper;

import com.example.backend.application.dto.group.GroupMemberCreateDto;
import com.example.backend.application.dto.group.GroupMemberResponseDto;
import com.example.backend.web.dto.group.GroupMemberAddRequest;
import com.example.backend.web.dto.group.GroupMemberResponse;

public class GroupMemberWebMapper {

  public static GroupMemberCreateDto toApp(Long groupId, GroupMemberAddRequest req) {
    return new GroupMemberCreateDto(groupId, req.userId());
  }

  public static GroupMemberResponse toWeb(GroupMemberResponseDto dto) {
    return new GroupMemberResponse(
            dto.groupMemberId(),
            dto.groupId(),
            dto.userId(),
            dto.joinedAt()
    );
  }
}
