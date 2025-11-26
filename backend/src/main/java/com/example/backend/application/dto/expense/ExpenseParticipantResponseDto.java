package com.example.backend.application.dto.expense;

import java.math.BigDecimal;

public record ExpenseParticipantResponseDto(
        Long expenseParticipantId,
        Long userId,
        BigDecimal shareAmount,
        String splitType
) {}
