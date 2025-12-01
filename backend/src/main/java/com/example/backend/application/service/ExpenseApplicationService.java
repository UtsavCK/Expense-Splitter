package com.example.backend.application.service;

import com.example.backend.application.dto.expense.*;
import com.example.backend.application.mapper.ExpenseMapper;
import com.example.backend.application.usecase.ExpenseUseCase;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.*;
import com.example.backend.domain.service.ExpenseDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseApplicationService implements ExpenseUseCase {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final ExpenseRepository expenseRepository;
  private final ExpenseParticipantRepository participantRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final ExpenseDomainService expenseDomainService;

  @Override
  public ExpenseResponseDto addExpense(ExpenseRequestDto dto) {
    // 1. Validate group exists
    Group group = groupRepository.findById(dto.groupId())
            .orElseThrow(() -> new IllegalArgumentException("Group not found!"));

    // 2. Validate payer exists and is a group member
    User paidBy = userRepository.findById(dto.paidBy())
            .orElseThrow(() -> new IllegalArgumentException("Payer not found!"));

    if (!groupMemberRepository.existsByGroupIdAndUserId(dto.groupId(), dto.paidBy())) {
      throw new IllegalArgumentException("Payer must be a member of the group!");
    }

    // 3. Get all group members for validation
    List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(dto.groupId());
    Set<Long> memberIds = groupMembers.stream()
            .map(m -> m.getUser().getUserId())
            .collect(Collectors.toSet());

    // 4. Validate all participants are group members
    for (ExpenseParticipantDto participantDto : dto.participants()) {
      if (!memberIds.contains(participantDto.userId())) {
        User user = userRepository.findById(participantDto.userId())
                .orElse(null);
        String userName = user != null ? user.getName() : "Unknown";
        throw new IllegalArgumentException(
                String.format("Participant %s (ID: %d) is not a member of this group!",
                        userName, participantDto.userId())
        );
      }
    }

    // 5. Create expense
    Expense domainExpense = ExpenseMapper.toDomain(dto, group, paidBy);

    // 6. Create participants
    List<ExpenseParticipant> participants = dto.participants().stream()
            .map(pdto -> {
              User user = userRepository.findById(pdto.userId())
                      .orElseThrow(() -> new IllegalArgumentException("Participant user not found!"));
              return ExpenseParticipant.builder()
                      .expense(domainExpense)
                      .user(user)
                      .shareAmount(pdto.shareAmount())
                      .splitType(pdto.splitType())
                      .build();
            })
            .toList();

    // 7. Validate split amounts
    expenseDomainService.validateSplit(domainExpense, participants);

    // 8. Save expense and participants
    Expense savedExpense = expenseRepository.save(domainExpense);

    List<ExpenseParticipant> savedParts = participants.stream()
            .map(p -> {
              p.setExpense(savedExpense);
              return participantRepository.save(p);
            })
            .toList();

    // 9. Convert to response DTO
    List<ExpenseParticipantResponseDto> partDtos = savedParts.stream()
            .map(ExpenseMapper::toParticipantDto)
            .toList();

    return ExpenseMapper.toDto(savedExpense, partDtos);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ExpenseResponseDto> getExpensesByGroup(Long groupId) {
    // Validate group exists
    groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    List<Expense> expenses = expenseRepository.findByGroupId(groupId);

    return expenses.stream()
            .map(expense -> {
              List<ExpenseParticipant> participants = participantRepository
                      .findByExpenseId(expense.getExpenseId());
              List<ExpenseParticipantResponseDto> participantDtos = participants.stream()
                      .map(ExpenseMapper::toParticipantDto)
                      .toList();
              return ExpenseMapper.toDto(expense, participantDtos);
            })
            .toList();
  }
}