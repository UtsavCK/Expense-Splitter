package com.example.backend.web.dto.user;

public record UserResponse(
        Long userId,
        String name,
        String email,
        String createdAt
) {}