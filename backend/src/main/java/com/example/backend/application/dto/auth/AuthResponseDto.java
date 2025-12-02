package com.example.backend.application.dto.auth;

public record AuthResponseDto(
        String token,
        Long userId,
        String email,
        String name
) {}
