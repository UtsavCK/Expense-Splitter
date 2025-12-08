package com.example.backend.web.dto.user;

public record DeletionEligibilityDto(
        Boolean canDelete,
        String status,
        String reason
) {}
