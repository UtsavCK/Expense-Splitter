package com.example.backend.application.dto.user;

public record UserSearchResultDto(
        Long userId,
        String name,
        String email
) {}
