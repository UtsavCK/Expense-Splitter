package com.example.backend.application.mapper;

import com.example.backend.application.dto.expense.ExpenseParticipantDto;
import com.example.backend.application.dto.expense.ExpenseParticipantResponseDto;
import com.example.backend.application.dto.expense.ExpenseRequestDto;
import com.example.backend.application.dto.expense.ExpenseResponseDto;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ExpenseMapper {
  private ExpenseMapper() {}

  public static Expense toDomain(ExpenseRequestDto dto, Group group, User paidByUser) {
    return Expense.builder()
            .group(group)
            .paidBy(paidByUser)
            .description(dto.description())
            .amount(dto.amount())
            .expenseDate(dto.expenseDate())
            .createdAt(LocalDateTime.now())
            .build();
  }

  public static ExpenseResponseDto toDto(Expense e, List<ExpenseParticipantResponseDto> parts){
    return new ExpenseResponseDto(
            e.getExpenseId(),
            e.getGroup().getGroupId(),
            e.getPaidBy().getUserId(),
            e.getPaidBy().getName(),
            e.getAmount(),
            e.getDescription(),
            e.getExpenseDate(),
            e.getCreatedAt(),
            parts
    );
  }

  public static ExpenseParticipant toParticipantDomain(ExpenseParticipantDto pd, Expense expense){
    return ExpenseParticipant.builder()
            .expense(expense)
            .user(null) // to be filled by service (fetch user by id)
            .shareAmount(pd.shareAmount())
            .splitType(pd.splitType())
            .build();
  }

  public static ExpenseParticipantResponseDto toParticipantDto(ExpenseParticipant p){
    return new ExpenseParticipantResponseDto(
            p.getExpenseParticipantId(),
            p.getUser().getUserId(),
            p.getUser().getName(),
            p.getShareAmount(),
            p.getSplitType());
  }
}
