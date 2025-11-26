package com.example.backend.web.dto.auth;

public record LoginResponse(
        String token,
        Long userId,
        String email,
        String name
) {}
