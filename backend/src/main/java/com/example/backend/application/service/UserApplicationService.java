package com.example.backend.application.service;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.user.*;
import com.example.backend.application.mapper.UserMapper;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserApplicationService implements UserUseCase {
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final ExpenseRepository expenseRepository;
  private final PaymentRepository paymentRepository;
  private final BalanceApplicationService balanceService;
  private final SettlementExecutionRepository settlementExecutionRepository; // ADD THIS
  private final PasswordEncoder encoder;

  @Override
  @Transactional(readOnly = true)
  public Optional<UserResponseDto> getUserById(Long id) {
    return userRepository.findById(id).map(UserMapper::toDto);
  }

  @Override
  public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (dto.name() != null && !dto.name().isBlank()) {
      user.setName(dto.name());
    }
    if (dto.newPassword() != null && !dto.newPassword().isBlank()) {
      if (dto.currentPassword() == null || dto.currentPassword().isBlank()) {
        throw new IllegalArgumentException("Current password is required to change password");
      }
      if (!encoder.matches(dto.currentPassword(), user.getPasswordHash())) {
        throw new IllegalArgumentException("Current password is incorrect");
      }
      user.setPasswordHash(encoder.encode(dto.newPassword()));
    }
    User updated = userRepository.save(user);
    return UserMapper.toDto(updated);
  }

  private void validateUserCanBeDeleted(Long userId) {
    log.info("Validating if user {} can be deleted", userId);

    List<BalanceDto> userBalances = balanceService.getUserBalances(userId);
    if (!userBalances.isEmpty()) {
      log.warn("User {} has {} remaining balances", userId, userBalances.size());
      throw new IllegalStateException(
              "Cannot delete account. You have " + userBalances.size() +
                      " outstanding balance(s). Please settle all debts before deleting your account."
      );
    }

    List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);
    List<Long> unsettledGroupIds = new ArrayList<>();

    for (GroupMember membership : memberships) {
      Long groupId = membership.getGroup().getGroupId();
      if (!settlementExecutionRepository.existsByGroupId(groupId)) {
        unsettledGroupIds.add(groupId);
      }
    }

    if (!unsettledGroupIds.isEmpty()) {
      log.warn("User {} has {} unsettled groups", userId, unsettledGroupIds.size());
      throw new IllegalStateException(
              "Cannot delete account. You have " + unsettledGroupIds.size() +
                      " unsettled group(s). Please settle all groups before deleting your account."
      );
    }

    log.info("User {} passed deletion validation", userId);
  }

  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("User not found");
    }

    validateUserCanBeDeleted(id);

    log.info("Deleting user {}", id);
    userRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserResponseDto> getUserByEmail(String email) {
    return userRepository.findByEmail(email).map(UserMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSearchResultDto> searchUsers(String query) {
    List<User> users;
    if (query.contains("@")) {
      users = userRepository.findByEmail(query)
              .map(List::of)
              .orElse(List.of());
    } else {
      users = userRepository.findByNameContainingIgnoreCase(query);
    }
    return users.stream()
            .map(user -> new UserSearchResultDto(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail()
            ))
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatsDto getUserStats(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }
    int totalGroups = groupMemberRepository.findByUserId(userId).size();
    int totalExpenses = expenseRepository.findByPaidByUserId(userId).size();
    int totalPaymentsMade = paymentRepository.findByPaidBy(userId).size();
    int totalPaymentsReceived = paymentRepository.findByPaidTo(userId).size();
    BigDecimal totalPaid = expenseRepository.findByPaidByUserId(userId).stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    List<BalanceDto> balances = balanceService.getUserBalances(userId);
    BigDecimal totalOwed = BigDecimal.ZERO;
    BigDecimal totalOwing = BigDecimal.ZERO;
    for (BalanceDto balance : balances) {
      if (balance.fromUserId().equals(userId)) {
        totalOwing = totalOwing.add(balance.amount());
      } else if (balance.toUserId().equals(userId)) {
        totalOwed = totalOwed.add(balance.amount());
      }
    }
    BigDecimal netBalance = totalOwed.subtract(totalOwing);
    return new UserStatsDto(
            totalGroups,
            totalExpenses,
            totalPaymentsMade,
            totalPaymentsReceived,
            totalPaid,
            totalOwed,
            netBalance
    );
  }
}
