package com.example.backend.application.usecase;

import com.example.backend.application.dto.group.GroupRequestDto;
import com.example.backend.application.dto.group.GroupResponseDto;

public interface GroupUseCase {
  GroupResponseDto createGroup(GroupRequestDto dto);
}
