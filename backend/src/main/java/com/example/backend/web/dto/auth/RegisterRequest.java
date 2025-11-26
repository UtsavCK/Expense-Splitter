package com.example.backend.web.dto.auth;

public record RegisterRequest(
        String name,
        String email,
        String password
) {}
