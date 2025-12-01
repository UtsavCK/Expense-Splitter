package com.example.backend.web.dto.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseCreateRequest(
        Long groupId,
        Long paidBy,
        BigDecimal amount,
        String description,
        LocalDate expenseDate,
        List<ParticipantRequest> participants
) {
  public record ParticipantRequest(
          Long userId,
          BigDecimal shareAmount,
          String splitType
  ) {}
}
