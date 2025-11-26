package com.example.backend.domain.model.expense;

import com.example.backend.domain.model.BaseDomainEntity;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Expense extends BaseDomainEntity {
  private Long expenseId;
  private Group group;
  private String description;
  private BigDecimal amount;
  private User paidBy;
  private LocalDate expenseDate;
}
