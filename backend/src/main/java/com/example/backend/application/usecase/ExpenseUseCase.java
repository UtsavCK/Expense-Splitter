package com.example.backend.application.usecase;

import com.example.backend.application.dto.expense.ExpenseRequestDto;
import com.example.backend.application.dto.expense.ExpenseResponseDto;

public interface ExpenseUseCase {
  ExpenseResponseDto addExpense(ExpenseRequestDto dto);
}
