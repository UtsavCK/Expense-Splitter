package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
  List<PaymentEntity> findByPaidBy_UserId(Long userId);
  List<PaymentEntity> findByPaidTo_UserId(Long userId);
  List<PaymentEntity> findByGroup_GroupId(Long groupId);
}
