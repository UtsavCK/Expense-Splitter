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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    // Validate group exists
    Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    // Get all expenses for this group
    List<Expense> expenses = expenseRepository.findByGroupId(groupId);

    // Get all participants for these expenses
    List<ExpenseParticipant> allParticipants = expenses.stream()
            .flatMap(expense -> participantRepository.findByExpenseId(expense.getExpenseId()).stream())
            .toList();

    // Get all group members
    List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
    Set<Long> memberIds = members.stream()
            .map(m -> m.getUser().getUserId())
            .collect(Collectors.toSet());

    // Get payments between group members
    List<Payment> payments = memberIds.stream()
            .flatMap(userId -> paymentRepository.findByPaidBy(userId).stream())
            .filter(p -> memberIds.contains(p.getPaidTo().getUserId()))
            .distinct()
            .toList();

    // Calculate balances
    List<Balance> balances = balanceDomainService.calculateBalances(expenses, allParticipants, payments);

    // Filter balances to only include group members
    List<Balance> groupBalances = balances.stream()
            .filter(b -> memberIds.contains(b.getFromUserId()) && memberIds.contains(b.getToUserId()))
            .toList();

    // Convert to DTOs with user names
    List<BalanceDto> balanceDtos = groupBalances.stream()
            .map(balance -> {
              User fromUser = userRepository.findById(balance.getFromUserId())
                      .orElseThrow(() -> new IllegalStateException("User not found"));
              User toUser = userRepository.findById(balance.getToUserId())
                      .orElseThrow(() -> new IllegalStateException("User not found"));

              return new BalanceDto(
                      balance.getFromUserId(),
                      fromUser.getName(),
                      balance.getToUserId(),
                      toUser.getName(),
                      balance.getAmount()
              );
            })
            .toList();

    return new GroupBalanceSummaryDto(groupId, group.getName(), balanceDtos);
  }

  @Override
  public List<BalanceDto> getBalanceBetweenUsers(Long userId1, Long userId2) {
    // Validate users exist
    userRepository.findById(userId1)
            .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
    userRepository.findById(userId2)
            .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

    // Get all expenses where either user is the payer
    List<Expense> expenses = new ArrayList<>();
    // This would need a custom repository method, simplified here

    // Get participants
    List<ExpenseParticipant> participants = new ArrayList<>();

    // Get payments between these users
    List<Payment> payments = new ArrayList<>();
    payments.addAll(paymentRepository.findByPaidBy(userId1).stream()
            .filter(p -> p.getPaidTo().getUserId().equals(userId2))
            .toList());
    payments.addAll(paymentRepository.findByPaidBy(userId2).stream()
            .filter(p -> p.getPaidTo().getUserId().equals(userId1))
            .toList());

    // Calculate and return balances
    List<Balance> balances = balanceDomainService.calculateBalances(expenses, participants, payments);

    return balances.stream()
            .filter(b ->
                    (b.getFromUserId().equals(userId1) && b.getToUserId().equals(userId2)) ||
                            (b.getFromUserId().equals(userId2) && b.getToUserId().equals(userId1))
            )
            .map(this::toBalanceDto)
            .toList();
  }

  @Override
  public List<BalanceDto> getUserBalances(Long userId) {
    // Validate user exists
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Get all groups the user is a member of
    List<GroupMember> memberships = groupMemberRepository.findByGroupId(userId);

    List<BalanceDto> allBalances = new ArrayList<>();

    // Calculate balances for each group
    for (GroupMember membership : memberships) {
      GroupBalanceSummaryDto groupSummary = calculateGroupBalances(membership.getGroup().getGroupId());

      // Filter to only balances involving this user
      List<BalanceDto> userGroupBalances = groupSummary.balances().stream()
              .filter(b -> b.fromUserId().equals(userId) || b.toUserId().equals(userId))
              .toList();

      allBalances.addAll(userGroupBalances);
    }

    return allBalances;
  }

  private BalanceDto toBalanceDto(Balance balance) {
    User fromUser = userRepository.findById(balance.getFromUserId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
    User toUser = userRepository.findById(balance.getToUserId())
            .orElseThrow(() -> new IllegalStateException("User not found"));

    return new BalanceDto(
            balance.getFromUserId(),
            fromUser.getName(),
            balance.getToUserId(),
            toUser.getName(),
            balance.getAmount()
    );
  }
}
