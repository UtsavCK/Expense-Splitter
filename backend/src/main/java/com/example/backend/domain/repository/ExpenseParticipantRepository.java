package com.example.backend.domain.repository;

import com.example.backend.domain.model.expense.ExpenseParticipant;

import java.util.List;

public interface ExpenseParticipantRepository {
  ExpenseParticipant save(ExpenseParticipant expenseParticipant);
  List<ExpenseParticipant> findByExpenseId(Long expenseId);
}
