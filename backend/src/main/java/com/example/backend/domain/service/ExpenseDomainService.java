package com.example.backend.domain.service;

import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.expense.ExpenseParticipant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseDomainService {
  public void validateSplit(Expense expense, List<ExpenseParticipant> participants) {
    BigDecimal total = participants.stream()
            .map(ExpenseParticipant::getShareAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (total.compareTo(expense.getAmount()) != 0) {
      if (total.subtract(expense.getAmount()).abs().compareTo(new BigDecimal("0.01")) > 0) {
        throw new IllegalArgumentException("Split amounts do not match expense total.");
      }
    }
  }
}
