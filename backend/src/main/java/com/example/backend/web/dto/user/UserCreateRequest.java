package com.example.backend.web.dto.user;

public record UserCreateRequest(
        String name,
        String email,
        String password
) {}
