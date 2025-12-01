package com.example.backend.application.dto.balance;


import java.util.List;

public record GroupSettlementPlanDto(
        Long groupId,
        String groupName,
        List<SettlementSuggestionDto> suggestions,
        int originalTransactionCount,
        int optimizedTransactionCount,
        String summary
) {}
