package com.example.backend.web.controller;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.balance.GroupBalanceSummaryDto;
import com.example.backend.application.usecase.BalanceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
public class BalanceController {

  private final BalanceUseCase balanceUseCase;

  @GetMapping("/group/{groupId}")
  public ResponseEntity<GroupBalanceSummaryDto> getGroupBalances(@PathVariable Long groupId) {
    GroupBalanceSummaryDto summary = balanceUseCase.calculateGroupBalances(groupId);
    return ResponseEntity.ok(summary);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<BalanceDto>> getUserBalances(@PathVariable Long userId) {
    List<BalanceDto> balances = balanceUseCase.getUserBalances(userId);
    return ResponseEntity.ok(balances);
  }

  @GetMapping("/between")
  public ResponseEntity<List<BalanceDto>> getBalanceBetweenUsers(
          @RequestParam Long userId1,
          @RequestParam Long userId2
  ) {
    List<BalanceDto> balances = balanceUseCase.getBalanceBetweenUsers(userId1, userId2);
    return ResponseEntity.ok(balances);
  }
}
