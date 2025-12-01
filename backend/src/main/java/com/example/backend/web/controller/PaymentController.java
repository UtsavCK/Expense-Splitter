package com.example.backend.web.controller;

import com.example.backend.application.dto.payment.PaymentResponseDto;
import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.infrastructure.exception.ForbiddenException;
import com.example.backend.web.dto.payment.PaymentCreateRequest;
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
  private final GroupMemberRepository groupMemberRepository;

  @PostMapping
  public ResponseEntity<PaymentResponseDto> createPayment(
          @RequestBody PaymentCreateRequest req,
          @CurrentUser Long userId
  ) {
    // User must be either the payer or the payee
    if (!userId.equals(req.paidBy()) && !userId.equals(req.paidTo())) {
      throw new ForbiddenException("You can only record payments involving yourself");
    }

    return ResponseEntity.ok(paymentService.recordPayment(PaymentWebMapper.toApplication(req)));
  }

  @GetMapping("/my-payments")
  public ResponseEntity<List<PaymentResponse>> getMyPayments(@CurrentUser Long userId) {
    return ResponseEntity.ok(
            paymentService.getPaymentsByUser(userId).stream()
                    .map(PaymentWebMapper::toWeb)
                    .toList()
    );
  }
}
