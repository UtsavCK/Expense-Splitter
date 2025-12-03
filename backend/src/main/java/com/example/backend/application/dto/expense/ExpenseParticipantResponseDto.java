package com.example.backend.application.dto.expense;

import java.math.BigDecimal;

public record ExpenseParticipantResponseDto(
        Long expenseParticipantId,
        Long userId,
        String userName,
        BigDecimal shareAmount,
        String splitType
) {}
