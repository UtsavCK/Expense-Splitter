package com.example.backend.web.dto.group;

import java.time.LocalDateTime;

public record GroupMemberResponse(
        Long groupMemberId,
        Long groupId,
        Long userId,
        String userName,
        String userEmail,
        LocalDateTime joinedAt
) {}
