package com.example.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long paymentId;

  @ManyToOne
  @JoinColumn(name = "paid_by") // foreignKey = @ForeignKey(name = "fk_payment_from_user")
  private UserEntity paidBy;

  @ManyToOne
  @JoinColumn(name = "paid_to") // foreignKey = @ForeignKey(name = "fk_payment_to_user")
  private UserEntity paidTo;

  @Column(precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  private String notes;
}
