package com.example.backend.domain.repository;

import com.example.backend.domain.model.expense.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
  Expense save(Expense expense);
  Optional<Expense> findById(Long id);
  List<Expense> findByGroupId(Long groupId);
}
