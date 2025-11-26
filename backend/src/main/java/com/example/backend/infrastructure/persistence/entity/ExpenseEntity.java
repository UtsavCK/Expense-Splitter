package com.example.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "expense_id")
  private Long expenseId;

  @ManyToOne
  @JoinColumn(name = "group_id") // foreignKey = @ForeignKey(name = "fk_expense_group")
  private GroupEntity group;

  private String description;

  @Column(precision = 12, scale = 2)
  private BigDecimal amount;

  @ManyToOne
  @JoinColumn(name = "paid_by") // foreignKey = @ForeignKey(name = "fk_expense_paid_by")
  private UserEntity paidBy;

  @Column(name = "expense_date")
  private LocalDate expenseDate;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();
}
