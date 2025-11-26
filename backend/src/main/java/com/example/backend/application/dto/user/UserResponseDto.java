package com.example.backend.application.dto.user;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long userId,
        String email,
        String name,
        LocalDateTime createdAt
) {}
