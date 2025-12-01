package com.example.backend.application.usecase;

import com.example.backend.application.dto.balance.GroupSettlementPlanDto;
import com.example.backend.application.dto.balance.SettlementSuggestionDto;

import java.util.List;

public interface SettlementUseCase {
  GroupSettlementPlanDto generateSettlementPlan(Long groupId);
  List<SettlementSuggestionDto> getUserSettlementSuggestions(Long userId);
  void executeSettlementPlan(Long groupId);
}
