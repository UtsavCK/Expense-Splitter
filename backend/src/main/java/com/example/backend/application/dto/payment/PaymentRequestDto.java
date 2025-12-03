package com.example.backend.application.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequestDto(
        Long paidBy,
        Long paidTo,
        Long groupId,
        BigDecimal amount,
        LocalDate paymentDate,
        String notes
) {}
