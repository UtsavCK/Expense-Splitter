package com.example.backend.domain.model.payment;

import com.example.backend.domain.model.user.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
  private Long paymentId;
  private User paidBy;
  private User paidTo;
  private BigDecimal amount;
  private LocalDate paymentDate;
  private String notes;
}
