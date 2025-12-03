package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.infrastructure.persistence.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long> {
  List<ExpenseEntity> findByGroup_GroupId(Long groupId);
  List<ExpenseEntity> findByPaidBy_UserId(Long userId);
}
