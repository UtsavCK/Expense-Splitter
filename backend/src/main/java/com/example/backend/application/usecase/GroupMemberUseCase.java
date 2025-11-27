package com.example.backend.application.usecase;

import com.example.backend.application.dto.group.GroupMemberCreateDto;
import com.example.backend.application.dto.group.GroupMemberResponseDto;

import java.util.List;

public interface GroupMemberUseCase {
  GroupMemberResponseDto addMember(GroupMemberCreateDto dto);
  List<GroupMemberResponseDto> getMembers(Long groupId);
  void removeMember(Long groupId, Long userId);
}