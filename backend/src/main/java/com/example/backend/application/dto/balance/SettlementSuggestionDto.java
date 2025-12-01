package com.example.backend.application.dto.balance;

import java.math.BigDecimal;
import java.util.List;

public record SettlementSuggestionDto(
        Long fromUserId,
        String fromUserName,
        Long toUserId,
        String toUserName,
        BigDecimal amount,
        String description
) {}
