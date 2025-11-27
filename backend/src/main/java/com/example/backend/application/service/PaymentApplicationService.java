package com.example.backend.application.service;

import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.dto.payment.PaymentResponseDto;
import com.example.backend.application.mapper.PaymentMapper;
import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.domain.model.payment.Payment;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.PaymentRepository;
import com.example.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentApplicationService implements PaymentUseCase {

  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;

  public PaymentApplicationService (PaymentRepository paymentRepository, UserRepository userRepository) {
    this.paymentRepository = paymentRepository;
    this.userRepository = userRepository;
  }

  @Override
  public PaymentResponseDto recordPayment(PaymentRequestDto dto) {
    User paidBy = userRepository.findById(dto.paidBy())
            .orElseThrow(() -> new IllegalArgumentException("paidBy not found!"));
    User paidTo = userRepository.findById(dto.paidTo())
            .orElseThrow(() -> new IllegalArgumentException("paidTo not found!"));
    Payment domainPayment = PaymentMapper.toDomain(dto, paidBy, paidTo);
    var saved = paymentRepository.save(domainPayment);
    return PaymentMapper.toDto(saved);
  }

  @Override
  public List<PaymentResponseDto> getPaymentsByUser(Long userId) {
    return paymentRepository.findByPaidBy(userId).stream()
            .map(PaymentMapper::toDto)
            .toList();
  }
}
