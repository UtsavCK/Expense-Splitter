package com.example.backend.infrastructure.persistence.mapper;

import com.example.backend.domain.model.payment.Payment;
import com.example.backend.infrastructure.persistence.entity.PaymentEntity;

public class PaymentEntityMapper {
  private PaymentEntityMapper() {}

  public static PaymentEntity toEntity(Payment p) {
    return PaymentEntity.builder()
            .paymentId(p.getPaymentId())
            .paidBy(
                    p.getPaidBy() != null
                            ? UserEntityMapper.toEntity(p.getPaidBy())
                            : null
            )
            .paidTo(
                    p.getPaidTo() != null
                            ? UserEntityMapper.toEntity(p.getPaidTo())
                            : null
            )
            .group(
                    p.getGroup() != null
                            ? GroupEntityMapper.toEntity(p.getGroup())
                            : null
            )
            .amount(p.getAmount())
            .paymentDate(p.getPaymentDate())
            .notes(p.getNotes())
            .build();
  }

  public static Payment toDomain(PaymentEntity pe) {
    return Payment.builder()
            .paymentId(pe.getPaymentId())
            .paidBy(
                    pe.getPaidBy() != null
                            ? UserEntityMapper.toDomain(pe.getPaidBy())
                            : null
            )
            .paidTo(
                    pe.getPaidTo() != null
                            ? UserEntityMapper.toDomain(pe.getPaidTo())
                            : null
            )
            .group(
                    pe.getGroup() != null
                            ? GroupEntityMapper.toDomain(pe.getGroup())
                            : null
            )
            .amount(pe.getAmount())
            .paymentDate(pe.getPaymentDate())
            .notes(pe.getNotes())
            .build();
  }
}
