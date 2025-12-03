package com.example.backend.application.dto.user;

import java.math.BigDecimal;

public record UserStatsDto(
        int totalGroups,
        int totalExpenses,
        int totalPaymentsMade,
        int totalPaymentsReceived,
        BigDecimal totalPaid,
        BigDecimal totalOwed,
        BigDecimal netBalance
) {}
