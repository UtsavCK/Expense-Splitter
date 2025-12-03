package com.example.backend.web.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentResponse(
        Long paymentId,
        Long paidBy,
        String paidByName,
        Long paidTo,
        String paidToName,
        Long groupId,
        String groupName,
        BigDecimal amount,
        LocalDate paymentDate,
        String notes
) {}
