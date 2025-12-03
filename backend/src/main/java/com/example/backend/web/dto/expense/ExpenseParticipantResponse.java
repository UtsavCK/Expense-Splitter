package com.example.backend.web.dto.expense;

import java.math.BigDecimal;

public record ExpenseParticipantResponse(
        Long expenseParticipantId,
        Long userId,
        String userName,
        BigDecimal shareAmount,
        String splitType
) {}
