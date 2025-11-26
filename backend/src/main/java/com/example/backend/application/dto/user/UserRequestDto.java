package com.example.backend.application.dto.user;

public record UserRequestDto(
        String name,
        String email,
        String password
) {}
