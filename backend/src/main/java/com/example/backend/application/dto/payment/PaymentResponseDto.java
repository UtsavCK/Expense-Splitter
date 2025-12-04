package com.example.backend.application.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponseDto(
        Long paymentId,
        Long paidBy,
        String paidByName,
        Long paidTo,
        String paidToName,
        BigDecimal amount,
        LocalDate paymentDate,
        String notes
) {}
