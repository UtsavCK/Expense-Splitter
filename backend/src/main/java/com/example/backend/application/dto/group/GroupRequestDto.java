package com.example.backend.application.dto.group;

public record GroupRequestDto(
        String name,
        Long createdByUserId
) {}
