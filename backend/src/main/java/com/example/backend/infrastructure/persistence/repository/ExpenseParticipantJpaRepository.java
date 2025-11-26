package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.infrastructure.persistence.entity.ExpenseParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseParticipantJpaRepository extends JpaRepository<ExpenseParticipantEntity, Long> {
  List<ExpenseParticipantEntity> findByExpense_ExpenseId(Long expenseId);
}
