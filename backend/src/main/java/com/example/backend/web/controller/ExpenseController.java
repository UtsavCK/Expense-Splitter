package com.example.backend.web.controller;

import com.example.backend.application.usecase.ExpenseUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.dto.expense.*;
import com.example.backend.web.mapper.ExpenseWebMapper;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

  private final ExpenseUseCase expenseService;
  private final AuthorizationDomainService authService;

  @PostMapping
  public ResponseEntity<ExpenseResponse> create(
          @RequestBody ExpenseCreateRequest req,
          @CurrentUser Long userId
  ) {
    // Check if user is a member of the group
    authService.requireGroupAccess(userId, req.groupId(), "add expenses");

    var dto = ExpenseWebMapper.toApplication(req);
    var created = expenseService.addExpense(dto);
    return ResponseEntity.ok(ExpenseWebMapper.toWeb(created));
  }

  @GetMapping("/group/{groupId}")
  public List<ExpenseResponse> listByGroup(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);
    return expenseService.getExpensesByGroup(groupId).stream()
            .map(ExpenseWebMapper::toWeb)
            .toList();
  }
}
