package com.example.backend.web.dto.auth;

public record RegisterResponse(
        Long userId,
        String name,
        String email
) {}
