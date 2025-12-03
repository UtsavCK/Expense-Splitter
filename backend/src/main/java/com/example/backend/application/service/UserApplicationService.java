package com.example.backend.application.service;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.user.*;
import com.example.backend.application.mapper.UserMapper;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.ExpenseRepository;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.domain.repository.PaymentRepository;
import com.example.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService implements UserUseCase {

  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final ExpenseRepository expenseRepository;
  private final PaymentRepository paymentRepository;
  private final BalanceApplicationService balanceService;
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

  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("User not found");
    }
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
      // Search by email
      users = userRepository.findByEmail(query)
              .map(List::of)
              .orElse(List.of());
    } else {
      // Search by name (you'll need to add this to repository)
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
    // Validate user exists
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }

    // Count groups
    int totalGroups = groupMemberRepository.findByUserId(userId).size();

    // Count expenses where user is payer
    int totalExpenses = expenseRepository.findByPaidByUserId(userId).size();

    // Count payments
    int totalPaymentsMade = paymentRepository.findByPaidBy(userId).size();
    int totalPaymentsReceived = paymentRepository.findByPaidTo(userId).size();

    // Calculate total paid (sum of all expenses where user is payer)
    BigDecimal totalPaid = expenseRepository.findByPaidByUserId(userId).stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Calculate total owed and owing from balances
    List<BalanceDto> balances = balanceService.getUserBalances(userId);

    BigDecimal totalOwed = BigDecimal.ZERO;  // Others owe me
    BigDecimal totalOwing = BigDecimal.ZERO; // I owe others

    for (BalanceDto balance : balances) {
      if (balance.fromUserId().equals(userId)) {
        // I owe someone
        totalOwing = totalOwing.add(balance.amount());
      } else if (balance.toUserId().equals(userId)) {
        // Someone owes me
        totalOwed = totalOwed.add(balance.amount());
      }
    }

    // Net balance = what I'm owed - what I owe
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
