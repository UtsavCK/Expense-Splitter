package com.example.backend.web.dto.group;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(
        @NotBlank String name
) {}
