package com.example.backend.infrastructure.persistence.mapper;

import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.infrastructure.persistence.entity.ExpenseParticipantEntity;

public class ExpenseParticipantEntityMapper {
  private ExpenseParticipantEntityMapper() {}

  public static ExpenseParticipantEntity toEntity(ExpenseParticipant ep) {
    return ExpenseParticipantEntity.builder()
            .expenseParticipantId(ep.getExpenseParticipantId())
            .expense(
                    ep.getExpense() != null
                            ? ExpenseEntityMapper.toEntity(ep.getExpense())
                            : null
            )
            .user(
                    ep.getUser() != null
                            ? UserEntityMapper.toEntity(ep.getUser())
                            : null
            )
            .shareAmount(ep.getShareAmount())
            .splitType(ep.getSplitType())
            .build();
  }

  public static ExpenseParticipant toDomain(ExpenseParticipantEntity epe) {
    return ExpenseParticipant.builder()
            .expenseParticipantId(epe.getExpenseParticipantId())
            .expense(
                    epe.getExpense() != null
                            ? ExpenseEntityMapper.toDomain(epe.getExpense())
                            : null
            )
            .user(
                    epe.getUser() != null
                            ? UserEntityMapper.toDomain(epe.getUser())
                            : null
            )
            .shareAmount(epe.getShareAmount())
            .splitType(epe.getSplitType())
            .build();
  }
}
