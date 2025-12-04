package com.example.backend.application.service;

import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.dto.payment.PaymentResponseDto;
import com.example.backend.application.mapper.PaymentMapper;
import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.payment.Payment;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.PaymentRepository;
import com.example.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentApplicationService implements PaymentUseCase {
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;

  @Override
  public PaymentResponseDto recordPayment(PaymentRequestDto dto) {
    User paidBy = userRepository.findById(dto.paidBy())
            .orElseThrow(() -> new IllegalArgumentException("paidBy user not found!"));
    User paidTo = userRepository.findById(dto.paidTo())
            .orElseThrow(() -> new IllegalArgumentException("paidTo user not found!"));

    // Create payment without group association
    Payment domainPayment = Payment.builder()
            .paidBy(paidBy)
            .paidTo(paidTo)
            .amount(dto.amount())
            .paymentDate(dto.paymentDate())
            .notes(dto.notes())
            .build();

    Payment saved = paymentRepository.save(domainPayment);
    return PaymentMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDto> getPaymentsByUser(Long userId) {
    return paymentRepository.findByPaidBy(userId).stream()
            .map(PaymentMapper::toDto)
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDto> getPaymentsToUser(Long userId) {
    return paymentRepository.findByPaidTo(userId).stream()
            .map(PaymentMapper::toDto)
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentResponseDto> getAllUserPayments(Long userId) {
    List<PaymentResponseDto> allPayments = new ArrayList<>();
    allPayments.addAll(getPaymentsByUser(userId));
    allPayments.addAll(getPaymentsToUser(userId));
    return allPayments;
  }
}
