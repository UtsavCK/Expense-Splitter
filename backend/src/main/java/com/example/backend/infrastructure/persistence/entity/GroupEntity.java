package com.example.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id")
  private Long groupId;

  private String name;

  @ManyToOne
  @JoinColumn(name = "created_by")  // foreignKey = @ForeignKey(name = "fk_group_creator") not required when table is already defined
  private UserEntity createdBy;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();
}
