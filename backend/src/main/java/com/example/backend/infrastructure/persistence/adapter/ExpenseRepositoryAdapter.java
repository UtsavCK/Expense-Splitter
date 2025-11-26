package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.repository.ExpenseRepository;
import com.example.backend.infrastructure.persistence.entity.ExpenseEntity;
import com.example.backend.infrastructure.persistence.mapper.ExpenseEntityMapper;
import com.example.backend.infrastructure.persistence.repository.ExpenseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseRepositoryAdapter implements ExpenseRepository {
  private ExpenseJpaRepository jpaRepo;

  public ExpenseRepositoryAdapter(ExpenseJpaRepository expenseJpaRepository) {
    this.jpaRepo = expenseJpaRepository;
  }

  @Override
  public Expense save(Expense expense) {
    ExpenseEntity saved = jpaRepo.save(ExpenseEntityMapper.toEntity(expense));
    return ExpenseEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<Expense> findById(Long id) {
    return jpaRepo.findById(id).map(ExpenseEntityMapper::toDomain);
  }

  @Override
  public List<Expense> findByGroupId(Long groupId) {
    return jpaRepo.findByGroup_GroupId(groupId)
            .stream()
            .map(ExpenseEntityMapper::toDomain)
            .toList();
  }
}
