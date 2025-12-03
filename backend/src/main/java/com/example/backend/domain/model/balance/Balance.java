package com.example.backend.domain.model.balance;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Balance {
  private Long fromUserId;
  private Long toUserId;
  private Long groupId;
  private BigDecimal amount; // positive means fromUser owes toUser
}
