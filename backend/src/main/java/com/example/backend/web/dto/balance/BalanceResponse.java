package com.example.backend.web.dto.balance;

import java.math.BigDecimal;

public record BalanceResponse(
        Long fromUserId,
        String fromUserName,
        Long toUserId,
        String toUserName,
        BigDecimal amount,
        String formattedAmount
) {
  public static BalanceResponse from(
          Long fromUserId,
          String fromUserName,
          Long toUserId,
          String toUserName,
          BigDecimal amount
  ) {
    return new BalanceResponse(
            fromUserId,
            fromUserName,
            toUserId,
            toUserName,
            amount,
            String.format("$%.2f", amount)
    );
  }
}
