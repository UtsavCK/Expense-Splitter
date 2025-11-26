package com.example.backend.application.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseResponseDto(
        Long expenseId,
        Long groupId,
        Long paidBy,
        BigDecimal amount,
        String description,
        LocalDate expenseDate,
        LocalDateTime createdAt,
        List<ExpenseParticipantResponseDto> participants
) {}
