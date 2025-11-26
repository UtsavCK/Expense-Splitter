package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.domain.repository.ExpenseParticipantRepository;
import com.example.backend.infrastructure.persistence.entity.ExpenseParticipantEntity;
import com.example.backend.infrastructure.persistence.mapper.ExpenseParticipantEntityMapper;
import com.example.backend.infrastructure.persistence.repository.ExpenseParticipantJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseParticipantRepositoryAdapter implements ExpenseParticipantRepository {
  private ExpenseParticipantJpaRepository jpaRepo;

  public ExpenseParticipantRepositoryAdapter(ExpenseParticipantJpaRepository expenseParticipantJpaRepository) {
    this.jpaRepo = expenseParticipantJpaRepository;
  }

  @Override
  public ExpenseParticipant save(ExpenseParticipant expenseParticipant) {
    ExpenseParticipantEntity saved = jpaRepo.save(ExpenseParticipantEntityMapper.toEntity(expenseParticipant));
    return ExpenseParticipantEntityMapper.toDomain(saved);
  }

  @Override
  public List<ExpenseParticipant> findByExpenseId(Long expenseId) {
    return jpaRepo.findByExpense_ExpenseId(expenseId)
            .stream()
            .map(ExpenseParticipantEntityMapper::toDomain)
            .toList();
  }
}
