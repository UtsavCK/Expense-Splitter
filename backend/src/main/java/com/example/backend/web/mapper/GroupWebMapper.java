package com.example.backend.web.mapper;

import com.example.backend.application.dto.group.GroupRequestDto;
import com.example.backend.application.dto.group.GroupResponseDto;
import com.example.backend.web.dto.group.GroupCreateRequest;
import com.example.backend.web.dto.group.GroupResponse;

public class GroupWebMapper {

  public static GroupRequestDto toApplication(GroupCreateRequest req, Long creatorId) {
    return new GroupRequestDto(
            req.name(),
            creatorId
    );
  }

  public static GroupResponse toWeb(GroupResponseDto dto) {
    return new GroupResponse(
            dto.groupId(),
            dto.name(),
            dto.createdByUserId(),
            dto.createdAt().toString()
    );
  }
}
