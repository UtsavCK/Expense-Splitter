package com.example.backend.application.dto.balance;

import java.math.BigDecimal;

public record BalanceDto(
        Long fromUserId,
        String fromUserName,
        Long toUserId,
        String toUserName,
        Long groupId,
        String groupName,
        BigDecimal amount
) {}
