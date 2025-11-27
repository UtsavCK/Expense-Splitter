package com.example.backend.application.dto.user;

public record UserUpdateDto(
        String name,
        String email,
        String passwordHash
) {}
