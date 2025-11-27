package com.example.backend.application.usecase;

import com.example.backend.application.dto.expense.ExpenseRequestDto;
import com.example.backend.application.dto.expense.ExpenseResponseDto;

import java.util.List;

public interface ExpenseUseCase {
  ExpenseResponseDto addExpense(ExpenseRequestDto dto);
  List<ExpenseResponseDto> getExpensesByGroup(Long groupId);
}
