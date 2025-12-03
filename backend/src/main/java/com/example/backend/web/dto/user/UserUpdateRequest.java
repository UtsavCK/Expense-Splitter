package com.example.backend.web.dto.user;

public record UserUpdateRequest(
        String name,
        String email,
        String currentPassword,
        String newPassword
) {}
