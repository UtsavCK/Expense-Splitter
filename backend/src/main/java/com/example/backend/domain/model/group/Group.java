package com.example.backend.domain.model.group;

import com.example.backend.domain.model.BaseDomainEntity;
import com.example.backend.domain.model.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Group extends BaseDomainEntity {
  private Long groupId;
  private String name;
  private User createdBy;
}
