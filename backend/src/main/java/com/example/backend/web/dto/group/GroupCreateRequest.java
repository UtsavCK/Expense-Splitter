package com.example.backend.web.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GroupCreateRequest(
        @NotBlank String name,
        @NotNull Long createdByUserId
) {}
