package com.example.backend.web.dto.expense;

import java.math.BigDecimal;

public record ExpenseParticipantRequest(
        Long userId,
        BigDecimal shareAmount,
        String splitType
) {}
