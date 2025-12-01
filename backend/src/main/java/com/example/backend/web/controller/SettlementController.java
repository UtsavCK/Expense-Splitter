package com.example.backend.web.controller;

import com.example.backend.application.dto.balance.GroupSettlementPlanDto;
import com.example.backend.application.dto.balance.SettlementSuggestionDto;
import com.example.backend.application.usecase.SettlementUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

  private final SettlementUseCase settlementUseCase;

  @GetMapping("/group/{groupId}/plan")
  public ResponseEntity<GroupSettlementPlanDto> getSettlementPlan(@PathVariable Long groupId) {
    GroupSettlementPlanDto plan = settlementUseCase.generateSettlementPlan(groupId);
    return ResponseEntity.ok(plan);
  }

  @GetMapping("/user/{userId}/suggestions")
  public ResponseEntity<List<SettlementSuggestionDto>> getUserSuggestions(@PathVariable Long userId) {
    List<SettlementSuggestionDto> suggestions = settlementUseCase.getUserSettlementSuggestions(userId);
    return ResponseEntity.ok(suggestions);
  }

  @PostMapping("/group/{groupId}/execute")
  public ResponseEntity<Void> executeSettlement(@PathVariable Long groupId) {
    settlementUseCase.executeSettlementPlan(groupId);
    return ResponseEntity.noContent().build();
  }
}
