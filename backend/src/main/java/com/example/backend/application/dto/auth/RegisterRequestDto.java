package com.example.backend.application.dto.auth;

public record RegisterRequestDto(
        String name,
        String email,
        String password
) {}
