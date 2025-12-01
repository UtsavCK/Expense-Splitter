package com.example.backend.application.dto.balance;

import java.util.List;

public record GroupBalanceSummaryDto(
        Long groupId,
        String groupName,
        List<BalanceDto> balances
) {}
