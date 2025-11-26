package com.example.backend.application.dto.group;

import java.time.LocalDateTime;

public record GroupResponseDto(
        Long groupId,
        String name,
        Long createdByUserId,
        LocalDateTime createdAt
) {}
