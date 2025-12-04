package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.infrastructure.persistence.entity.SettlementExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementExecutionJpaRepository extends JpaRepository<SettlementExecutionEntity, Long> {
  boolean existsByGroupId(Long groupId);
}
