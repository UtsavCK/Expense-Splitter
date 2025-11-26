package com.example.backend.domain.model.group;

import com.example.backend.domain.model.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMember {
  private Long groupMemberId;
  private Group group;
  private User user;
  private LocalDateTime joinedAt = LocalDateTime.now();
}
