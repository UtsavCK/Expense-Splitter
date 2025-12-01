package com.example.backend.web.controller;

import com.example.backend.application.dto.balance.GroupSettlementPlanDto;
import com.example.backend.application.dto.balance.SettlementSuggestionDto;
import com.example.backend.application.usecase.SettlementUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController {

  private final SettlementUseCase settlementUseCase;
  private final AuthorizationDomainService authService;

  /**
   * Get settlement plan - only group members can see
   */
  @GetMapping("/group/{groupId}/plan")
  public ResponseEntity<GroupSettlementPlanDto> getSettlementPlan(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);
    GroupSettlementPlanDto plan = settlementUseCase.generateSettlementPlan(groupId);
    return ResponseEntity.ok(plan);
  }

  /**
   * Get own settlement suggestions
   */
  @GetMapping("/my-suggestions")
  public ResponseEntity<List<SettlementSuggestionDto>> getMySuggestions(@CurrentUser Long userId) {
    List<SettlementSuggestionDto> suggestions = settlementUseCase.getUserSettlementSuggestions(userId);
    return ResponseEntity.ok(suggestions);
  }

  /**
   * Execute settlement - only group members can execute
   */
  @PostMapping("/group/{groupId}/execute")
  public ResponseEntity<Void> executeSettlement(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupAccess(userId, groupId, "execute settlement");
    settlementUseCase.executeSettlementPlan(groupId);
    return ResponseEntity.noContent().build();
  }
}
