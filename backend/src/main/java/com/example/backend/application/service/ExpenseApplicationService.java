package com.example.backend.application.service;

import com.example.backend.application.dto.expense.*;
import com.example.backend.application.mapper.ExpenseMapper;
import com.example.backend.application.usecase.ExpenseUseCase;
import com.example.backend.domain.model.expense.Expense;
import com.example.backend.domain.model.expense.ExpenseParticipant;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.ExpenseParticipantRepository;
import com.example.backend.domain.repository.ExpenseRepository;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.domain.service.ExpenseDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseApplicationService implements ExpenseUseCase {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final ExpenseRepository expenseRepository;
  private final ExpenseParticipantRepository participantRepository;
  private final ExpenseDomainService expenseDomainService;

  public ExpenseApplicationService(
          UserRepository userRepository,
          GroupRepository groupRepository,
          ExpenseRepository expenseRepository,
          ExpenseParticipantRepository participantRepository,
          ExpenseDomainService expenseDomainService
  ) {
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
    this.expenseRepository = expenseRepository;
    this.participantRepository = participantRepository;
    this.expenseDomainService = expenseDomainService;
  }

  @Override
  public ExpenseResponseDto addExpense(ExpenseRequestDto dto) {
    Group group = groupRepository.findById(dto.groupId())
            .orElseThrow(() -> new IllegalArgumentException("Group not found!"));
    User paidBy = userRepository.findById(dto.paidBy())
            .orElseThrow(() -> new IllegalArgumentException("Payer not found!"));
    Expense domainExpense = ExpenseMapper.toDomain(dto, group, paidBy);

    List<ExpenseParticipant> participants = dto.participants()
            .stream()
            .map(pdto -> {
              User user = userRepository.findById(pdto.userId())
                      .orElseThrow(() -> new IllegalArgumentException("Participant user not found!"));
              return ExpenseParticipant.builder()
                      .expense(domainExpense)
                      .user(user)
                      .shareAmount(pdto.shareAmount())
                      .splitType(pdto.splitType())
                      .build();
            }).toList();
    expenseDomainService.validateSplit(domainExpense, participants);

    var savedExpense = expenseRepository.save(domainExpense);

    List<ExpenseParticipant> savedParts = participants.stream()
            .map(p -> {
              p.setExpense(savedExpense);
              return participantRepository.save(p);
            })
            .toList();

    var partDtos = savedParts.stream()
            .map(ExpenseMapper::toParticipantDto)
            .toList();

    return ExpenseMapper.toDto(savedExpense, partDtos);
  }

  @Override
  public List<ExpenseResponseDto> getExpensesByGroup(Long groupId) {
    var expenses = expenseRepository.findByGroupId(groupId);

    return expenses.stream()
            .map(expense -> {
              var participants = participantRepository.findByExpenseId(expense.getExpenseId());
              var participantDtos = participants.stream()
                      .map(ExpenseMapper::toParticipantDto)
                      .toList();
              return ExpenseMapper.toDto(expense, participantDtos);
            })
            .toList();
  }
}
