package com.example.backend.application.service;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.balance.GroupBalanceSummaryDto;
import com.example.backend.application.usecase.BalanceUseCase;
import com.example.backend.domain.model.balance.Balance;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.model.payment.Payment;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.*;
import com.example.backend.domain.service.BalanceDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BalanceApplicationService implements BalanceUseCase {

  private final ExpenseRepository expenseRepository;
  private final ExpenseParticipantRepository participantRepository;
  private final PaymentRepository paymentRepository;
  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final BalanceDomainService balanceDomainService;

  @Override
  public GroupBalanceSummaryDto calculateGroupBalances(Long groupId) {
    log.info("=== Calculating balances for group: {} ===", groupId);

    // Validate group exists
    Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    log.info("Group found: {}", group.getName());

    // Get all expenses for this group
    List<Expense> expenses = expenseRepository.findByGroupId(groupId);
    log.info("Found {} expenses for group", expenses.size());
    expenses.forEach(e -> log.debug("  Expense: {} - Amount: {}, Paid by: {}",
            e.getExpenseId(), e.getAmount(), e.getPaidBy().getName()));

    // Get all participants for these expenses
    List<ExpenseParticipant> allParticipants = expenses.stream()
            .flatMap(expense -> {
              List<ExpenseParticipant> parts = participantRepository.findByExpenseId(expense.getExpenseId());
              log.debug("  Expense {} has {} participants", expense.getExpenseId(), parts.size());
              return parts.stream();
            })
            .toList();
    log.info("Found {} total participants", allParticipants.size());

    // Get all group members
    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
    Set<Long> memberIds = members.stream()
            .map(m -> m.getUser().getUserId())
            .collect(Collectors.toSet());
    log.info("Group has {} members: {}", memberIds.size(), memberIds);

    // Get payments between group members
    List<Payment> payments = memberIds.stream()
            .flatMap(userId -> paymentRepository.findByPaidBy(userId).stream())
            .filter(p -> memberIds.contains(p.getPaidTo().getUserId()))
            .distinct()
            .toList();
    log.info("Found {} payments", payments.size());

    // Calculate balances
    log.info("Calling balanceDomainService.calculateBalances()");
    List<Balance> balances = balanceDomainService.calculateBalances(
            groupId,
            expenses, allParticipants, payments
    );
    log.info("BalanceDomainService returned {} balances", balances.size());
    balances.forEach(b -> log.debug("  Balance: {} -> {} = {}",
            b.getFromUserId(), b.getToUserId(), b.getAmount()));

    // Filter balances to only include group members
    List<Balance> groupBalances = balances.stream()
            .filter(b -> memberIds.contains(b.getFromUserId()) && memberIds.contains(b.getToUserId()))
            .toList();
    log.info("After filtering to group members: {} balances", groupBalances.size());

    // Convert to DTOs with user names
    List<BalanceDto> balanceDtos = groupBalances.stream()
            .map(this::toBalanceDto)
            .toList();
    log.info("Converted to {} balance DTOs", balanceDtos.size());

    return new GroupBalanceSummaryDto(groupId, group.getName(), balanceDtos);
  }

  @Override
  public List<BalanceDto> getBalanceBetweenUsers(Long currentUserId, Long otherUserId) {
    log.info("=== Calculating balance between users: {} and {} ===", currentUserId, otherUserId);

    userRepository.findById(currentUserId)
            .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
    userRepository.findById(otherUserId)
            .orElseThrow(() -> new IllegalArgumentException("Other user not found"));

    List<Expense> expenses = new ArrayList<>();
    List<ExpenseParticipant> participants = new ArrayList<>();

    List<Payment> payments = new ArrayList<>();
    payments.addAll(paymentRepository.findByPaidBy(currentUserId).stream()
            .filter(p -> p.getPaidTo().getUserId().equals(otherUserId))
            .toList());
    payments.addAll(paymentRepository.findByPaidBy(otherUserId).stream()
            .filter(p -> p.getPaidTo().getUserId().equals(currentUserId))
            .toList());

    log.info("Found {} payments between users", payments.size());

    Long groupId = null;

    List<Balance> balances = balanceDomainService.calculateBalances(
            groupId,
            expenses, participants, payments
    );
    log.info("BalanceDomainService returned {} balances", balances.size());

    return balances.stream()
            .filter(b ->
                    (b.getFromUserId().equals(currentUserId) && b.getToUserId().equals(otherUserId)) ||
                            (b.getFromUserId().equals(otherUserId) && b.getToUserId().equals(currentUserId))
            )
            .map(this::toBalanceDto)
            .toList();
  }

  @Override
  public List<BalanceDto> getUserBalances(Long userId) {
    log.info("=== Calculating all balances for user: {} ===", userId);

    userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);
    log.info("User is member of {} groups", memberships.size());

    List<BalanceDto> allBalances = new ArrayList<>();
    for (GroupMember membership : memberships) {
      log.info("Processing group: {}", membership.getGroup().getGroupId());
      GroupBalanceSummaryDto groupSummary = calculateGroupBalances(
              membership.getGroup().getGroupId()
      );
      List<BalanceDto> userGroupBalances = groupSummary.balances().stream()
              .filter(b -> b.fromUserId().equals(userId) || b.toUserId().equals(userId))
              .toList();
      log.info("  User has {} balances in this group", userGroupBalances.size());
      allBalances.addAll(userGroupBalances);
    }
    return allBalances;
  }

  private BalanceDto toBalanceDto(Balance balance) {
    User fromUser = userRepository.findById(balance.getFromUserId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    User toUser = userRepository.findById(balance.getToUserId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    Group group = groupRepository.findById(balance.getGroupId())
            .orElseThrow(() -> new IllegalStateException(("Group not found")));

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
