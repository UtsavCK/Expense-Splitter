package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.settlement.SettlementExecution;
import com.example.backend.domain.repository.SettlementExecutionRepository;
import com.example.backend.infrastructure.persistence.entity.SettlementExecutionEntity;
import com.example.backend.infrastructure.persistence.repository.SettlementExecutionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SettlementExecutionRepositoryAdapter implements SettlementExecutionRepository {
  private final SettlementExecutionJpaRepository jpaRepository;

  @Override
  public SettlementExecution save(SettlementExecution execution) {
    SettlementExecutionEntity entity = SettlementExecutionEntity.builder()
            .groupId(execution.getGroupId())
            .executedAt(execution.getExecutedAt())
            .build();
    SettlementExecutionEntity saved = jpaRepository.save(entity);
    return new SettlementExecution(saved.getExecutionId(), saved.getGroupId(), saved.getExecutedAt());
  }

  @Override
  public boolean existsByGroupId(Long groupId) {
    return jpaRepository.existsByGroupId(groupId);
  }
}
