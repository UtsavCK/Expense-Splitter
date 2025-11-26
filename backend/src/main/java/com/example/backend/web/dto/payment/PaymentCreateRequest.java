package com.example.backend.web.dto.payment;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentCreateRequest(
        @NotNull Long paidBy,
        @NotNull Long paidTo,
        @NotNull BigDecimal amount,
        @NotNull LocalDate paymentDate,
        String notes
) {}
