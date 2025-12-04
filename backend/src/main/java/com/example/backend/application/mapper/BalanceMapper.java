package com.example.backend.application.mapper;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.domain.model.balance.Balance;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;

public class BalanceMapper {
  private BalanceMapper() {}

  public static BalanceDto toDto(Balance balance, User fromUser, User toUser, Group group) {
    return new BalanceDto(
            balance.getFromUserId(),
            fromUser.getName(),
            balance.getToUserId(),
            toUser.getName(),
            balance.getGroupId(),
            group.getName(),
            balance.getAmount()
    );
  }
}
