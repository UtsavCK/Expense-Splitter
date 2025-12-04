package com.example.backend.domain.repository;

import com.example.backend.domain.model.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
  Payment save(Payment payment);
  Optional<Payment> findById(Long id);
  List<Payment> findByPaidBy(Long userId);
  List<Payment> findByPaidTo(Long userId);
}
