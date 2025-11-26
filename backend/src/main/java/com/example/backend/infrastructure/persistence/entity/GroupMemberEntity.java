package com.example.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"group_id", "user_id"}, name = "unique_group_user")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_member_id")
  private Long groupMemberId;

  @ManyToOne
  @JoinColumn(name = "group_id") //foreignKey = @ForeignKey(name = "fk_group_members_group")
  private GroupEntity group;

  @ManyToOne
  @JoinColumn(name = "user_id") // foreignKey = @ForeignKey(name = "fk_group_members_user")
  private UserEntity user;

  @Column(name = "joined_at")
  private LocalDateTime joinedAt = LocalDateTime.now();
}
