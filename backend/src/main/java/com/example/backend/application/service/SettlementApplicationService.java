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
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.settlement.SettlementExecution;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.domain.repository.SettlementExecutionRepository;
import com.example.backend.domain.service.BalanceDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementApplicationService implements SettlementUseCase {
  private final BalanceUseCase balanceUseCase;
  private final PaymentUseCase paymentUseCase;
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final BalanceDomainService balanceDomainService;
  private final SettlementExecutionRepository settlementExecutionRepository;

  @Override
  @Transactional
  public void executeSettlementPlan(Long groupId) {
    // Prevent multiple settlements on same group
    if (settlementExecutionRepository.existsByGroupId(groupId)) {
      throw new IllegalStateException(
              "Settlement has already been executed for this group. Settlement can only be executed once."
      );
    }

    GroupSettlementPlanDto plan = generateSettlementPlan(groupId);
    if (plan.suggestions().isEmpty()) {
      return;
    }

    LocalDate today = LocalDate.now();
    for (SettlementSuggestionDto suggestion : plan.suggestions()) {
      // Create payment without group reference
      PaymentRequestDto paymentRequest = new PaymentRequestDto(
              suggestion.fromUserId(),
              suggestion.toUserId(),
              suggestion.amount(),
              today,
              "Settlement: " + suggestion.description()
      );
      paymentUseCase.recordPayment(paymentRequest);
    }

    // Record that settlement was executed
    SettlementExecution execution = new SettlementExecution(null, groupId, LocalDateTime.now());
    settlementExecutionRepository.save(execution);
  }

  @Override
  @Transactional(readOnly = true)
  public GroupSettlementPlanDto generateSettlementPlan(Long groupId) {
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

    List<Balance> balances = balanceSummary.balances().stream()
            .map(dto -> Balance.builder()
                    .fromUserId(dto.fromUserId())
                    .toUserId(dto.toUserId())
                    .groupId(dto.groupId())
                    .amount(dto.amount())
                    .build())
            .toList();

    int originalCount = balances.size();
    List<Balance> simplified = balanceDomainService.simplifyBalances(balances);

    List<SettlementSuggestionDto> suggestions = simplified.stream()
            .map(balance -> {
              User fromUser = userRepository.findById(balance.getFromUserId())
                      .orElseThrow(() -> new IllegalStateException("User not found"));
              User toUser = userRepository.findById(balance.getToUserId())
                      .orElseThrow(() -> new IllegalStateException("User not found"));
              Group group = groupRepository.findById(balance.getGroupId())
                      .orElseThrow(() -> new IllegalStateException("Group not found"));
              String description = String.format(
                      "%s pays %s",
                      fromUser.getName(),
                      toUser.getName()
              );
              return new SettlementSuggestionDto(
                      balance.getFromUserId(),
                      fromUser.getName(),
                      balance.getToUserId(),
                      toUser.getName(),
                      balance.getGroupId(),
                      group.getName(),
                      balance.getAmount(),
                      description
              );
            }).toList();

    String summary = String.format(
            "Optimized from %d to %d transactions. Total: $%.2f",
            originalCount,
            simplified.size(),
            simplified.stream().map(Balance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
    );

    return new GroupSettlementPlanDto(groupId, balanceSummary.groupName(), suggestions, originalCount, simplified.size(), summary);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SettlementSuggestionDto> getUserSettlementSuggestions(Long userId) {
    List<BalanceDto> userBalances = balanceUseCase.getUserBalances(userId);
    Map<String, BigDecimal> aggregated = new HashMap<>();

    for (BalanceDto balance : userBalances) {
      String key = balance.fromUserId() + "-" + balance.toUserId() + "-" + balance.groupId();
      BigDecimal amount = balance.fromUserId().equals(userId) ? balance.amount() : balance.amount().negate();
      aggregated.merge(key, amount, BigDecimal::add);
    }

    return aggregated.entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) != 0)
            .map(entry -> {
              String[] ids = entry.getKey().split("-");
              Long fromId = Long.parseLong(ids[0]);
              Long toId = Long.parseLong(ids[1]);
              Long groupId = Long.parseLong(ids[2]);
              BigDecimal amount = entry.getValue();

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
              Group group = groupRepository.findById(groupId)
                      .orElseThrow(() -> new IllegalStateException("Group not found"));

              return new SettlementSuggestionDto(fromId, fromUser.getName(), toId, toUser.getName(),
                      groupId, group.getName(), amount, "Settlement suggestion");
            }).toList();
  }
}
