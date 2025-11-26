package com.example.backend.web.dto.expense;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ExpenseParticipantRequest(
        @NotNull Long userId,
        @NotNull BigDecimal shareAmount,
        @NotBlank String splitType
) {}
