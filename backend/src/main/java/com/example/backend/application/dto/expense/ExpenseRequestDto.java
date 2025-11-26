package com.example.backend.application.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseRequestDto(
        Long groupId,
        Long paidBy,
        BigDecimal amount,
        String description,
        LocalDate expenseDate,
        List<ExpenseParticipantDto> participants
) {}
