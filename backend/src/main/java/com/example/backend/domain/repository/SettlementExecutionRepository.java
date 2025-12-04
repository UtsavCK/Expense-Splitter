package com.example.backend.domain.repository;

import com.example.backend.domain.model.settlement.SettlementExecution;

public interface SettlementExecutionRepository {
  SettlementExecution save(SettlementExecution execution);
  boolean existsByGroupId(Long groupId);
}