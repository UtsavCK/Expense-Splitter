package com.example.backend.web.controller;

import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.web.dto.payment.PaymentResponse;
import com.example.backend.web.mapper.PaymentWebMapper;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentUseCase paymentService;

  @GetMapping("/my-payments")
  public ResponseEntity<List<PaymentResponse>> getMyPayments(@CurrentUser Long userId) {
    return ResponseEntity.ok(
            paymentService.getPaymentsByUser(userId).stream()
                    .map(PaymentWebMapper::toWeb)
                    .toList()
    );
  }
}