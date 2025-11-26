package com.example.backend.web.controller;

import com.example.backend.application.usecase.ExpenseUseCase;
import com.example.backend.web.dto.expense.*;
import com.example.backend.web.mapper.ExpenseWebMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

  private final ExpenseUseCase expenses;

  public ExpenseController(ExpenseUseCase expenses) {
    this.expenses = expenses;
  }

  @PostMapping
  public ExpenseResponse create(@Valid @RequestBody ExpenseCreateRequest req) {
    return ExpenseWebMapper.toWeb(
            expenses.addExpense(ExpenseWebMapper.toApplication(req))
    );
  }
}
