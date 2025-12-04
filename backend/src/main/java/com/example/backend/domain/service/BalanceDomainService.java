package com.example.backend.domain.service;

import com.example.backend.domain.model.balance.Balance;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.domain.model.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BalanceDomainService {

  public List<Balance> calculateBalances(
          Long groupId,
          List<Expense> expenses,
          List<ExpenseParticipant> allParticipants,
          List<Payment> payments
  ) {
    log.info("=== BalanceDomainService.calculateBalances() ===");
    log.info("Input: groupId={}, expenses={}, participants={}, payments={}",
            groupId, expenses.size(), allParticipants.size(), payments.size());

    Map<String, BigDecimal> netBalances = new HashMap<>();

    for (Expense expense : expenses) {
      Long payerId = expense.getPaidBy().getUserId();
      Long expenseGroupId = expense.getGroup().getGroupId();
      log.debug("Processing expense {}: paidBy={}, groupId={}, amount={}",
              expense.getExpenseId(), payerId, expenseGroupId, expense.getAmount());

      List<ExpenseParticipant> expenseParticipants = allParticipants.stream()
              .filter(p -> p.getExpense().getExpenseId().equals(expense.getExpenseId()))
              .toList();

      for (ExpenseParticipant participant : expenseParticipants) {
        Long participantId = participant.getUser().getUserId();
        BigDecimal participantShare = participant.getShareAmount();
        if (payerId.equals(participantId)) {
          log.debug("  Skipping participant {} (is payer)", participantId);
          continue;
        }
        String key = participantId + "->" + payerId + ":" + expenseGroupId;
        log.debug("  Adding balance key: {} amount: {}", key, participantShare);
        netBalances.merge(key, participantShare, BigDecimal::add);
      }
    }

    log.info("After expenses, netBalances has {} entries", netBalances.size());
    netBalances.forEach((k, v) -> log.debug("  {} = {}", k, v));

    for (Payment payment : payments) {
      Long fromUserId = payment.getPaidBy().getUserId();
      Long toUserId = payment.getPaidTo().getUserId();
      String key = fromUserId + "->" + toUserId + ":";
      log.debug("Subtracting payment {} for key: {}", payment.getAmount(), key);
      netBalances.merge(key, payment.getAmount().negate(), BigDecimal::add);
    }

    log.info("After payments, netBalances has {} entries", netBalances.size());
    netBalances.forEach((k, v) -> log.debug("  {} = {}", k, v));

    Map<String, BigDecimal> consolidated = new HashMap<>();
    Set<String> processed = new HashSet<>();

    for (Map.Entry<String, BigDecimal> entry : netBalances.entrySet()) {
      String key = entry.getKey();
      if (processed.contains(key)) {
        continue;
      }

      try {
        // Parse composite key: userId1->userId2:groupId
        String[] parts = key.split(":");
        if (parts.length != 2) {
          log.error("Invalid key format: {} - expected format: userId->userId:groupId", key);
          continue;
        }

        String userPart = parts[0];
        Long keyGroupId = Long.parseLong(parts[1]);

        String[] userIds = userPart.split("->");
        if (userIds.length != 2) {
          log.error("Invalid user part format: {} - expected format: userId->userId", userPart);
          continue;
        }

        Long userId1 = Long.parseLong(userIds[0]);
        Long userId2 = Long.parseLong(userIds[1]);

        BigDecimal debt12 = netBalances.getOrDefault(userId1 + "->" + userId2 + ":" + keyGroupId, BigDecimal.ZERO);
        BigDecimal debt21 = netBalances.getOrDefault(userId2 + "->" + userId1 + ":" + keyGroupId, BigDecimal.ZERO);
        BigDecimal netDebt = debt12.subtract(debt21);

        log.debug("Consolidating: {}->{}:{} debt12={}, debt21={}, netDebt={}",
                userId1, userId2, keyGroupId, debt12, debt21, netDebt);

        processed.add(userId1 + "->" + userId2 + ":" + keyGroupId);
        processed.add(userId2 + "->" + userId1 + ":" + keyGroupId);

        if (netDebt.compareTo(BigDecimal.ZERO) != 0) {
          String consolidatedKey = getBalanceKey(userId1, userId2, keyGroupId);
          consolidated.put(consolidatedKey, netDebt.abs());
        }
      } catch (Exception e) {
        log.error("Error processing key: {}", key, e);
      }
    }

    log.info("Consolidated has {} entries", consolidated.size());
    consolidated.forEach((k, v) -> log.debug("  {} = {}", k, v));

    List<Balance> balances = new ArrayList<>();
    for (Map.Entry<String, BigDecimal> entry : consolidated.entrySet()) {
      BigDecimal amount = entry.getValue().setScale(2, RoundingMode.HALF_UP);
      if (amount.compareTo(BigDecimal.ZERO) != 0) {
        try {
          String[] parts = entry.getKey().split(":");
          String userPart = parts[0];
          Long keyGroupId = Long.parseLong(parts[1]);

          String[] userIds = userPart.split("-");
          Long userId1 = Long.parseLong(userIds[0]);
          Long userId2 = Long.parseLong(userIds[1]);

          BigDecimal debt12 = netBalances.getOrDefault(userId1 + "->" + userId2 + ":" + keyGroupId, BigDecimal.ZERO);
          BigDecimal debt21 = netBalances.getOrDefault(userId2 + "->" + userId1 + ":" + keyGroupId, BigDecimal.ZERO);
          BigDecimal netDebt = debt12.subtract(debt21);

          if (netDebt.compareTo(BigDecimal.ZERO) > 0) {
            balances.add(Balance.builder()
                    .fromUserId(userId1)
                    .toUserId(userId2)
                    .groupId(keyGroupId)
                    .amount(amount)
                    .build());
            log.debug("Created balance: {} -> {} = {}", userId1, userId2, amount);
          } else {
            balances.add(Balance.builder()
                    .fromUserId(userId2)
                    .toUserId(userId1)
                    .groupId(keyGroupId)
                    .amount(amount)
                    .build());
            log.debug("Created balance: {} -> {} = {}", userId2, userId1, amount);
          }
        } catch (Exception e) {
          log.error("Error creating balance for key: {}", entry.getKey(), e);
        }
      }
    }

    log.info("Returning {} balances", balances.size());
    return balances;
  }

  private String getBalanceKey(Long userId1, Long userId2, Long groupId) {
    if (userId1.compareTo(userId2) < 0) {
      return userId1 + "-" + userId2 + ":" + groupId;
    } else {
      return userId2 + "-" + userId1 + ":" + groupId;
    }
  }

  public List<Balance> simplifyBalances(List<Balance> balances) {
    // Group balances by groupId for independent simplification
    Map<Long, List<Balance>> balancesByGroup = balances.stream()
            .collect(Collectors.groupingBy(Balance::getGroupId));

    List<Balance> allSimplified = new ArrayList<>();

    for (List<Balance> groupBalances : balancesByGroup.values()) {
      allSimplified.addAll(simplifyBalancesForGroup(groupBalances));
    }

    return allSimplified;
  }

  private List<Balance> simplifyBalancesForGroup(List<Balance> balances) {
    if (balances.isEmpty()) {
      return new ArrayList<>();
    }

    Long groupId = balances.get(0).getGroupId();

    Map<Long, BigDecimal> netPosition = new HashMap<>();
    for (Balance balance : balances) {
      netPosition.merge(balance.getFromUserId(), balance.getAmount().negate(), BigDecimal::add);
      netPosition.merge(balance.getToUserId(), balance.getAmount(), BigDecimal::add);
    }

    List<Map.Entry<Long, BigDecimal>> debtors = netPosition.entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) < 0)
            .collect(Collectors.toList());

    List<Map.Entry<Long, BigDecimal>> creditors = netPosition.entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList());

    List<Balance> simplified = new ArrayList<>();
    int i = 0, j = 0;
    while (i < debtors.size() && j < creditors.size()) {
      Long debtorId = debtors.get(i).getKey();
      BigDecimal debt = debtors.get(i).getValue().abs();
      Long creditorId = creditors.get(j).getKey();
      BigDecimal credit = creditors.get(j).getValue();
      BigDecimal transferAmount = debt.min(credit);

      simplified.add(Balance.builder()
              .fromUserId(debtorId)
              .toUserId(creditorId)
              .groupId(groupId)
              .amount(transferAmount.setScale(2, RoundingMode.HALF_UP))
              .build());

      debt = debt.subtract(transferAmount);
      credit = credit.subtract(transferAmount);

      if (debt.compareTo(BigDecimal.ZERO) == 0) {
        i++;
      } else {
        debtors.get(i).setValue(debt.negate());
      }

      if (credit.compareTo(BigDecimal.ZERO) == 0) {
        j++;
      } else {
        creditors.get(j).setValue(credit);
      }
    }

    return simplified;
  }
}