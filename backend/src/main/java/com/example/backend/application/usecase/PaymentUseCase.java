package com.example.backend.application.usecase;

import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.dto.payment.PaymentResponseDto;

import java.util.List;

public interface PaymentUseCase {
  PaymentResponseDto recordPayment(PaymentRequestDto dto);
  List<PaymentResponseDto> getPaymentsByUser(Long userId);
  List<PaymentResponseDto> getPaymentsToUser(Long userId);
  List<PaymentResponseDto> getAllUserPayments(Long userId);
}
