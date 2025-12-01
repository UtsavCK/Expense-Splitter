package com.example.backend.web.controller;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.balance.GroupBalanceSummaryDto;
import com.example.backend.application.usecase.BalanceUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
public class BalanceController {

  private final BalanceUseCase balanceUseCase;
  private final AuthorizationDomainService authService;

  @GetMapping("/group/{groupId}")
  public ResponseEntity<GroupBalanceSummaryDto> getGroupBalances(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);
    GroupBalanceSummaryDto summary = balanceUseCase.calculateGroupBalances(groupId);
    return ResponseEntity.ok(summary);
  }

  @GetMapping("/my-balances")
  public ResponseEntity<List<BalanceDto>> getMyBalances(@CurrentUser Long userId) {
    List<BalanceDto> balances = balanceUseCase.getUserBalances(userId);
    return ResponseEntity.ok(balances);
  }

  @GetMapping("/with/{otherUserId}")
  public ResponseEntity<List<BalanceDto>> getBalanceWithUser(
          @PathVariable Long otherUserId,
          @CurrentUser Long userId
  ) {
    // Users can only see balances involving themselves
    List<BalanceDto> balances = balanceUseCase.getBalanceBetweenUsers(userId, otherUserId);
    return ResponseEntity.ok(balances);
  }
}