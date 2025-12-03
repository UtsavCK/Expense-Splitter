package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.payment.Payment;
import com.example.backend.domain.repository.PaymentRepository;
import com.example.backend.infrastructure.persistence.entity.PaymentEntity;
import com.example.backend.infrastructure.persistence.mapper.PaymentEntityMapper;
import com.example.backend.infrastructure.persistence.repository.PaymentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {
  private PaymentJpaRepository jpaRepo;

  public PaymentRepositoryAdapter(PaymentJpaRepository paymentJpaRepository) {
    this.jpaRepo = paymentJpaRepository;
  }

  @Override
  public Payment save(Payment payment) {
    PaymentEntity saved = jpaRepo.save(PaymentEntityMapper.toEntity(payment));
    return PaymentEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<Payment> findById(Long id) {
    return jpaRepo.findById(id).map(PaymentEntityMapper::toDomain);
  }

  @Override
  public List<Payment> findByPaidBy(Long userId) {
    return jpaRepo.findByPaidBy_UserId(userId)
            .stream()
            .map(PaymentEntityMapper::toDomain)
            .toList();
  }

  @Override
  public List<Payment> findByPaidTo(Long userId) {
    return jpaRepo.findByPaidTo_UserId(userId)
            .stream()
            .map(PaymentEntityMapper::toDomain)
            .toList();
  }

  @Override
  public List<Payment> findByGroup(Long groupId) {
    return jpaRepo.findByGroup_GroupId(groupId)
            .stream()
            .map(PaymentEntityMapper::toDomain)
            .toList();
  }
}
