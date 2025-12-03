package com.example.backend.web.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentCreateRequest(
        Long paidBy,
        Long paidTo,
        Long groupId,
        BigDecimal amount,
        LocalDate paymentDate,
        String notes
) {}
