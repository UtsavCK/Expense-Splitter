package com.example.backend.domain.model.settlement;

import java.time.LocalDateTime;

public class SettlementExecution {
  private Long executionId;
  private Long groupId;
  private LocalDateTime executedAt;

  public SettlementExecution(Long executionId, Long groupId, LocalDateTime executedAt) {
    this.executionId = executionId;
    this.groupId = groupId;
    this.executedAt = executedAt;
  }

  public Long getExecutionId() {
    return executionId;
  }

  public Long getGroupId() {
    return groupId;
  }

  public LocalDateTime getExecutedAt() {
    return executedAt;
  }
}
