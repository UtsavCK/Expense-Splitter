package com.example.backend.application.dto.group;

public record GroupMemberCreateDto(
        Long groupId,
        Long userId
) {}
