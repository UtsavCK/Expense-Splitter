package com.example.backend.application.mapper;

import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.dto.payment.PaymentResponseDto;
import com.example.backend.domain.model.payment.Payment;
import com.example.backend.domain.model.user.User;

public class PaymentMapper {
  private PaymentMapper() {}

  public static Payment toDomain(PaymentRequestDto dto, User paidBy, User paidTo) {
    return Payment.builder()
            .paidBy(paidBy)
            .paidTo(paidTo)
            .amount(dto.amount())
            .paymentDate(dto.paymentDate())
            .notes(dto.notes())
            .build();
  }

  public static PaymentResponseDto toDto(Payment payment) {
    return new PaymentResponseDto(
            payment.getPaymentId(),
            payment.getPaidBy().getUserId(),
            payment.getPaidTo().getUserId(),
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getNotes()
    );
  }
}
