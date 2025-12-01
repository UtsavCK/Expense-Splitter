package com.example.backend.application.service;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.balance.GroupBalanceSummaryDto;
import com.example.backend.application.dto.balance.GroupSettlementPlanDto;
import com.example.backend.application.dto.balance.SettlementSuggestionDto;
import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.usecase.BalanceUseCase;
import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.application.usecase.SettlementUseCase;
import com.example.backend.domain.model.balance.Balance;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.domain.service.BalanceDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SettlementApplicationService implements SettlementUseCase {

  private final BalanceUseCase balanceUseCase;
  private final PaymentUseCase paymentUseCase;
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final BalanceDomainService balanceDomainService;

  @Override
  @Transactional(readOnly = true)
  public GroupSettlementPlanDto generateSettlementPlan(Long groupId) {
    // Get current balances
    GroupBalanceSummaryDto balanceSummary = balanceUseCase.calculateGroupBalances(groupId);

    if (balanceSummary.balances().isEmpty()) {
      return new GroupSettlementPlanDto(
              groupId,
              balanceSummary.groupName(),
              Collections.emptyList(),
              0,
              0,
              "Group is already settled! No payments needed."
      );
    }

    // Convert to domain Balance objects for simplification
    List<Balance> balances = balanceSummary.balances().stream()
            .map(dto -> Balance.builder()
                    .fromUserId(dto.fromUserId())
                    .toUserId(dto.toUserId())
                    .amount(dto.amount())
                    .build())
            .toList();

    int originalCount = balances.size();

    // Simplify balances
    List<Balance> simplified = balanceDomainService.simplifyBalances(balances);

    // Convert to settlement suggestions
    List<SettlementSuggestionDto> suggestions = simplified.stream()
            .map(balance -> {
              User fromUser = userRepository.findById(balance.getFromUserId())
                      .orElseThrow(() -> new IllegalStateException("User not found"));
              User toUser = userRepository.findById(balance.getToUserId())
                      .orElseThrow(() -> new IllegalStateException("User not found"));

              String description = String.format(
                      "%s should pay %s to settle expenses",
                      fromUser.getName(),
                      toUser.getName()
              );

              return new SettlementSuggestionDto(
                      balance.getFromUserId(),
                      fromUser.getName(),
                      balance.getToUserId(),
                      toUser.getName(),
                      balance.getAmount(),
                      description
              );
            })
            .toList();

    String summary = String.format(
            "Optimized from %d to %d transactions. Total settlement amount: $%.2f",
            originalCount,
            simplified.size(),
            simplified.stream()
                    .map(Balance::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
    );

    return new GroupSettlementPlanDto(
            groupId,
            balanceSummary.groupName(),
            suggestions,
            originalCount,
            simplified.size(),
            summary
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<SettlementSuggestionDto> getUserSettlementSuggestions(Long userId) {
    // Get all user balances
    List<BalanceDto> userBalances = balanceUseCase.getUserBalances(userId);

    // Group by debtor-creditor pairs
    Map<String, BigDecimal> aggregated = new HashMap<>();

    for (BalanceDto balance : userBalances) {
      String key;
      BigDecimal amount;

      if (balance.fromUserId().equals(userId)) {
        // User owes someone
        key = balance.fromUserId() + "-" + balance.toUserId();
        amount = balance.amount();
      } else {
        // Someone owes user
        key = balance.fromUserId() + "-" + balance.toUserId();
        amount = balance.amount().negate();
      }

      aggregated.merge(key, amount, BigDecimal::add);
    }

    // Convert to suggestions
    return aggregated.entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) != 0)
            .map(entry -> {
              String[] ids = entry.getKey().split("-");
              Long fromId = Long.parseLong(ids[0]);
              Long toId = Long.parseLong(ids[1]);
              BigDecimal amount = entry.getValue();

              // Flip if negative
              if (amount.compareTo(BigDecimal.ZERO) < 0) {
                Long temp = fromId;
                fromId = toId;
                toId = temp;
                amount = amount.abs();
              }

              User fromUser = userRepository.findById(fromId)
                      .orElseThrow(() -> new IllegalStateException("User not found"));
              User toUser = userRepository.findById(toId)
                      .orElseThrow(() -> new IllegalStateException("User not found"));

              return new SettlementSuggestionDto(
                      fromId,
                      fromUser.getName(),
                      toId,
                      toUser.getName(),
                      amount,
                      "Suggested settlement payment"
              );
            })
            .toList();
  }

  @Override
  @Transactional
  public void executeSettlementPlan(Long groupId) {
    GroupSettlementPlanDto plan = generateSettlementPlan(groupId);

    if (plan.suggestions().isEmpty()) {
      return; // Already settled
    }

    // Record each suggested payment
    LocalDate today = LocalDate.now();

    for (SettlementSuggestionDto suggestion : plan.suggestions()) {
      PaymentRequestDto paymentRequest = new PaymentRequestDto(
              suggestion.fromUserId(),
              suggestion.toUserId(),
              suggestion.amount(),
              today,
              "Settlement payment - " + suggestion.description()
      );

      paymentUseCase.recordPayment(paymentRequest);
    }
  }
}
