package com.example.backend.web.controller;

import com.example.backend.application.dto.payment.PaymentResponseDto;
import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.web.dto.payment.PaymentCreateRequest;
import com.example.backend.web.dto.payment.PaymentResponse;
import com.example.backend.web.mapper.PaymentWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentUseCase paymentService;

  public PaymentController(PaymentUseCase paymentUseCase) {
    this.paymentService = paymentUseCase;
  }

  @PostMapping
  public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentCreateRequest req) {
    return ResponseEntity.ok(paymentService.recordPayment(PaymentWebMapper.toApplication(req)));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentResponse>> getUserPayments(@PathVariable Long userId) {
    return ResponseEntity.ok(
            paymentService.getPaymentsByUser(userId)
                    .stream()
                    .map(PaymentWebMapper::toWeb)
                    .toList()
            );
  }
}
