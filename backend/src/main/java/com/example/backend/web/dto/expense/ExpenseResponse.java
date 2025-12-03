package com.example.backend.web.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseResponse(
        Long expenseId,
        Long groupId,
        Long paidBy,
        String paidByName,
        BigDecimal amount,
        String description,
        LocalDate expenseDate,
        String createdAt,
        List<ExpenseParticipantResponse> participants
) {}
