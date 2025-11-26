package com.example.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "expense_participants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"expense_id", "user_id"}, name = "unique_expense_user")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseParticipantEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "expense_participant_id")
  private Long expenseParticipantId;

  @ManyToOne
  @JoinColumn(name = "expense_id") // foreignKey = @ForeignKey(name = "fk_participant_expense")
  private ExpenseEntity expense;

  @ManyToOne
  @JoinColumn(name = "user_id") // foreignKey = @ForeignKey(name = "fk_participant_user")
  private UserEntity user;

  @Column(name = "share_amount", precision = 12, scale = 2)
  private BigDecimal shareAmount;

  @Column(name = "split_type", length = 20)
  private String splitType;
}
