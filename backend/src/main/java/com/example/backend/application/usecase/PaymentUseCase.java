package com.example.backend.application.usecase;

import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.dto.payment.PaymentResponseDto;

public interface PaymentUseCase {
  PaymentResponseDto recordPayment(PaymentRequestDto dto);
}
