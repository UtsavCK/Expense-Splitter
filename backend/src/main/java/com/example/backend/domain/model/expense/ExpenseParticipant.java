package com.example.backend.domain.model.expense;

import com.example.backend.domain.model.user.User;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseParticipant {
  private Long expenseParticipantId;
  private Expense expense;
  private User user;
  private BigDecimal shareAmount;
  private String splitType;
}
