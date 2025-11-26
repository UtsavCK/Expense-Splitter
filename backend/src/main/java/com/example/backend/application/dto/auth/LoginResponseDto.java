package com.example.backend.application.dto.auth;

public record LoginResponseDto(
        String token,
        Long userId,
        String email,
        String name
) {}
