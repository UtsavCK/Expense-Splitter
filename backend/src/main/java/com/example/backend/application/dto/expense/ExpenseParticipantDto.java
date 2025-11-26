package com.example.backend.application.dto.expense;

import java.math.BigDecimal;

public record ExpenseParticipantDto(
        Long userId,
        BigDecimal shareAmount,
        String splitType
) {}
