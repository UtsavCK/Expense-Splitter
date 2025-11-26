package com.example.backend.web.controller;

import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.web.dto.payment.*;
import com.example.backend.web.mapper.PaymentWebMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

  private final PaymentUseCase payments;

  public PaymentController(PaymentUseCase payments) {
    this.payments = payments;
  }

  @PostMapping
  public PaymentResponse create(@RequestBody PaymentCreateRequest req) {
    return PaymentWebMapper.toWeb(
            payments.recordPayment(PaymentWebMapper.toApplication(req))
    );
  }
}
