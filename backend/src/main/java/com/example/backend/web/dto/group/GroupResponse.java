package com.example.backend.web.dto.group;

public record GroupResponse(
        Long groupId,
        String name,
        Long createdByUserId,
        String createdAt
) {}
