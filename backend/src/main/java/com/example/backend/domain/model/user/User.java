package com.example.backend.domain.model.user;

import com.example.backend.domain.model.BaseDomainEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends BaseDomainEntity {
  private Long userId;
  private String name;
  private String email;
  private String passwordHash;
}
