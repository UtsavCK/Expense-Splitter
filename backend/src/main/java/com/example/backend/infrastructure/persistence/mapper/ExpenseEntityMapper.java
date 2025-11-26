package com.example.backend.infrastructure.persistence.mapper;

import com.example.backend.domain.model.expense.Expense;
import com.example.backend.infrastructure.persistence.entity.ExpenseEntity;

public class ExpenseEntityMapper {
  private ExpenseEntityMapper() {}

  public static ExpenseEntity toEntity(Expense e) {
    return ExpenseEntity.builder()
            .expenseId(e.getExpenseId())
            .group(
                    e.getGroup() != null
                            ? GroupEntityMapper.toEntity(e.getGroup())
                            : null
            )
            .description(e.getDescription())
            .amount(e.getAmount())
            .paidBy(
                    e.getPaidBy() != null
                            ? UserEntityMapper.toEntity(e.getPaidBy())
                            : null
            )
            .expenseDate(e.getExpenseDate())
            .createdAt(e.getCreatedAt())
            .build();
  }

  public static Expense toDomain(ExpenseEntity ee) {
    return Expense.builder()
            .expenseId(ee.getExpenseId())
            .group(
                    ee.getGroup() != null
                            ? GroupEntityMapper.toDomain(ee.getGroup())
                            : null
            )
            .description(ee.getDescription())
            .amount(ee.getAmount())
            .paidBy(
                    ee.getPaidBy() != null
                            ? UserEntityMapper.toDomain(ee.getPaidBy())
                            : null
            )
            .expenseDate(ee.getExpenseDate())
            .createdAt(ee.getCreatedAt())
            .build();
  }
}
