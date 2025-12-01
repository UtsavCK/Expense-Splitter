package com.example.backend.application.usecase;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.balance.GroupBalanceSummaryDto;

import java.util.List;

public interface BalanceUseCase {

  GroupBalanceSummaryDto calculateGroupBalances(Long groupId);
  List<BalanceDto> getBalanceBetweenUsers(Long currentUserId, Long otherUserId);
  List<BalanceDto> getUserBalances(Long userId);
}
