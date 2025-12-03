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

  /**
   * Calculate net balances from expenses and payments
   * Core algorithm:
   * 1. For each expense, payer is owed by each participant
   * 2. Payments reduce debts
   * 3. Net balances show final state
   */
  public List<Balance> calculateBalances(
          List<Expense> expenses,
          List<ExpenseParticipant> allParticipants,
          List<Payment> payments
  ) {
    // Map to track net balances: Key = "fromUserId->toUserId", Value = amount
    // This is DIRECTIONAL - we track who owes whom
    Map<String, BigDecimal> netBalances = new HashMap<>();

    // Step 1: Process expenses
    for (Expense expense : expenses) {
      Long payerId = expense.getPaidBy().getUserId();

      // Get participants for this expense
      List<ExpenseParticipant> expenseParticipants = allParticipants.stream()
              .filter(p -> p.getExpense().getExpenseId().equals(expense.getExpenseId()))
              .toList();

      for (ExpenseParticipant participant : expenseParticipants) {
        Long participantId = participant.getUser().getUserId();
        BigDecimal participantShare = participant.getShareAmount();

        // Skip if payer is also participant (they don't owe themselves)
        if (payerId.equals(participantId)) {
          continue;
        }

        // Participant owes payer their share
        // Use DIRECTIONAL key: participant->payer
        String key = participantId + "->" + payerId;
        netBalances.merge(key, participantShare, BigDecimal::add);
      }
    }

    // Step 2: Process payments (reduce debts)
    for (Payment payment : payments) {
      Long fromUserId = payment.getPaidBy().getUserId();
      Long toUserId = payment.getPaidTo().getUserId();

      // Payment reduces the debt from->to
      String key = fromUserId + "->" + toUserId;
      netBalances.merge(key, payment.getAmount().negate(), BigDecimal::add);
    }

    // Step 3: Consolidate bidirectional debts
    Map<String, BigDecimal> consolidated = new HashMap<>();
    Set<String> processed = new HashSet<>();

    for (Map.Entry<String, BigDecimal> entry : netBalances.entrySet()) {
      String key = entry.getKey();
      if (processed.contains(key)) {
        continue;
      }

      String[] parts = key.split("->");
      Long userId1 = Long.parseLong(parts[0]);
      Long userId2 = Long.parseLong(parts[1]);

      // Get debt in both directions
      BigDecimal debt12 = netBalances.getOrDefault(userId1 + "->" + userId2, BigDecimal.ZERO);
      BigDecimal debt21 = netBalances.getOrDefault(userId2 + "->" + userId1, BigDecimal.ZERO);

      // Calculate net debt
      BigDecimal netDebt = debt12.subtract(debt21);

      // Mark both directions as processed
      processed.add(userId1 + "->" + userId2);
      processed.add(userId2 + "->" + userId1);

      // Store consolidated debt if non-zero
      if (netDebt.compareTo(BigDecimal.ZERO) != 0) {
        // Use consistent key (smaller ID first)
        String consolidatedKey = getBalanceKey(userId1, userId2);

        if (netDebt.compareTo(BigDecimal.ZERO) > 0) {
          // userId1 owes userId2
          consolidated.put(consolidatedKey, netDebt.abs());
        } else {
          // userId2 owes userId1 (debt is negative, so flip)
          consolidated.put(consolidatedKey, netDebt.abs());
        }
      }
    }

    // Step 4: Convert to Balance objects
    List<Balance> balances = new ArrayList<>();

    for (Map.Entry<String, BigDecimal> entry : consolidated.entrySet()) {
      BigDecimal amount = entry.getValue().setScale(2, RoundingMode.HALF_UP);

      // Only include non-zero balances
      if (amount.compareTo(BigDecimal.ZERO) != 0) {
        String[] userIds = entry.getKey().split("-");
        Long userId1 = Long.parseLong(userIds[0]);
        Long userId2 = Long.parseLong(userIds[1]);

        // Determine direction based on original net debt
        BigDecimal debt12 = netBalances.getOrDefault(userId1 + "->" + userId2, BigDecimal.ZERO);
        BigDecimal debt21 = netBalances.getOrDefault(userId2 + "->" + userId1, BigDecimal.ZERO);
        BigDecimal netDebt = debt12.subtract(debt21);

        if (netDebt.compareTo(BigDecimal.ZERO) > 0) {
          // userId1 owes userId2
          balances.add(Balance.builder()
                  .fromUserId(userId1)
                  .toUserId(userId2)
                  .amount(amount)
                  .build());
        } else {
          // userId2 owes userId1
          balances.add(Balance.builder()
                  .fromUserId(userId2)
                  .toUserId(userId1)
                  .amount(amount)
                  .build());
        }
      }
    }

    return balances;
  }

  /**
   * Create consistent key for user pair (always smaller ID first)
   */
  private String getBalanceKey(Long userId1, Long userId2) {
    if (userId1.compareTo(userId2) < 0) {
      return userId1 + "-" + userId2;
    } else {
      return userId2 + "-" + userId1;
    }
  }

  /**
   * Simplify balances using debt simplification algorithm
   * Minimizes number of transactions needed
   */
  public List<Balance> simplifyBalances(List<Balance> balances) {
    // Calculate net position for each user
    Map<Long, BigDecimal> netPosition = new HashMap<>();

    for (Balance balance : balances) {
      netPosition.merge(balance.getFromUserId(), balance.getAmount().negate(), BigDecimal::add);
      netPosition.merge(balance.getToUserId(), balance.getAmount(), BigDecimal::add);
    }

    // Separate debtors and creditors
    List<Map.Entry<Long, BigDecimal>> debtors = netPosition.entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) < 0)
            .collect(Collectors.toList());

    List<Map.Entry<Long, BigDecimal>> creditors = netPosition.entrySet().stream()
            .filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList());

    // Greedy algorithm: match debtors with creditors
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
