package com.example.backend.web.controller;

import com.example.backend.application.usecase.ExpenseUseCase;
import com.example.backend.web.dto.expense.*;
import com.example.backend.web.mapper.ExpenseWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseUseCase service;

  public ExpenseController(ExpenseUseCase service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ExpenseResponse> create(@RequestBody ExpenseCreateRequest req) {
    var dto = ExpenseWebMapper.toApplication(req);
    var created = service.addExpense(dto);
    return ResponseEntity.ok(ExpenseWebMapper.toWeb(created));
  }

  @GetMapping("/group/{groupId}")
  public List<ExpenseResponse> listByGroup(@PathVariable Long groupId) {
    return service.getExpensesByGroup(groupId)
            .stream()
            .map(ExpenseWebMapper::toWeb)
            .toList();
  }
}
