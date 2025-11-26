package com.example.backend.web.dto.expense;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseCreateRequest(
        @NotNull Long groupId,
        @NotNull Long paidBy,
        @NotNull BigDecimal amount,
        @NotBlank String description,
        @NotNull LocalDate expenseDate,
        @NotNull List<ExpenseParticipantRequest> participants
) {}
