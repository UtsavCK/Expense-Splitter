package com.example.backend.application.dto.group;

import java.time.LocalDateTime;

public record GroupMemberResponseDto(
        Long groupMemberId,
        Long groupId,
        Long userId,
        LocalDateTime joinedAt
) {}
