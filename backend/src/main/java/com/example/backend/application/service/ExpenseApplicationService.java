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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExpenseApplicationService implements ExpenseUseCase {
  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final ExpenseRepository expenseRepository;
  private final ExpenseParticipantRepository participantRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final SettlementExecutionRepository settlementExecutionRepository;
  private final ExpenseDomainService expenseDomainService;

  @Override
  public ExpenseResponseDto addExpense(ExpenseRequestDto dto) {
    log.info("Starting addExpense with dto: {}", dto);

    if (settlementExecutionRepository.existsByGroupId(dto.groupId())) {
      throw new IllegalStateException(
              "Cannot add expenses to this group. Settlement has already been executed. "
                      + "No further expenses can be added to a settled group."
      );
    }

    // 1. Validate group exists
    log.debug("Validating group: {}", dto.groupId());
    Group group = groupRepository.findById(dto.groupId())
            .orElseThrow(() -> new IllegalArgumentException("Group not found!"));
    log.debug("Group found: {}", group.getName());

    // 2. Validate payer exists and is a group member
    log.debug("Validating payer: {}", dto.paidBy());
    User paidBy = userRepository.findById(dto.paidBy())
            .orElseThrow(() -> new IllegalArgumentException("Payer not found!"));
    log.debug("Payer found: {}", paidBy.getName());

    if (!groupMemberRepository.existsByGroupIdAndUserId(dto.groupId(), dto.paidBy())) {
      throw new IllegalArgumentException("Payer must be a member of the group!");
    }

    // 3. Get all group members for validation
    List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(dto.groupId());
    Set<Long> memberIds = groupMembers.stream()
            .map(m -> m.getUser().getUserId())
            .collect(Collectors.toSet());
    log.debug("Group members: {}", memberIds);

    // 4. Validate all participants are group members
    log.debug("Validating {} participants", dto.participants().size());
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
    log.debug("Creating expense domain object");
    Expense domainExpense = ExpenseMapper.toDomain(dto, group, paidBy);
    log.debug("Expense created with id: {}, amount: {}", domainExpense.getExpenseId(), domainExpense.getAmount());

    // 6. Create participants
    log.debug("Creating {} participants", dto.participants().size());
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
    log.debug("Participants created: {}", participants.size());

    // 7. Validate split amounts
    log.debug("Validating split amounts");
    expenseDomainService.validateSplit(domainExpense, participants);
    log.debug("Split validation passed");

    // 8. Save expense and participants
    log.debug("Saving expense to repository");
    Expense savedExpense = expenseRepository.save(domainExpense);
    log.debug("Expense saved with id: {}", savedExpense.getExpenseId());

    log.debug("Saving {} participants", participants.size());
    List<ExpenseParticipant> savedParts = participants.stream()
            .map(p -> {
              p.setExpense(savedExpense);
              return participantRepository.save(p);
            })
            .toList();
    log.debug("Participants saved: {}", savedParts.size());

    // 9. Convert to response DTO
    log.debug("Converting to response DTO");
    List<ExpenseParticipantResponseDto> partDtos = savedParts.stream()
            .map(ExpenseMapper::toParticipantDto)
            .toList();

    ExpenseResponseDto response = ExpenseMapper.toDto(savedExpense, partDtos);
    log.info("Expense added successfully with id: {}", response.expenseId());
    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ExpenseResponseDto> getExpensesByGroup(Long groupId) {
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