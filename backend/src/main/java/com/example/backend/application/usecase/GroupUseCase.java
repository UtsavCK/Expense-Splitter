package com.example.backend.application.usecase;

import com.example.backend.application.dto.group.GroupRequestDto;
import com.example.backend.application.dto.group.GroupResponseDto;

import java.util.List;
import java.util.Optional;

public interface GroupUseCase {
  GroupResponseDto createGroup(GroupRequestDto dto);
  List<GroupResponseDto> getAllGroups();
  Optional<GroupResponseDto> getGroupById(Long id);
}
