package com.example.backend.web.mapper;

import com.example.backend.application.dto.expense.ExpenseParticipantDto;
import com.example.backend.application.dto.expense.ExpenseRequestDto;
import com.example.backend.application.dto.expense.ExpenseResponseDto;
import com.example.backend.web.dto.expense.ExpenseCreateRequest;
import com.example.backend.web.dto.expense.ExpenseParticipantResponse;
import com.example.backend.web.dto.expense.ExpenseResponse;

import java.util.List;

public class ExpenseWebMapper {

  public static ExpenseRequestDto toApplication(ExpenseCreateRequest req) {

    List<ExpenseParticipantDto> participants = req.participants().stream()
            .map(p -> new ExpenseParticipantDto(
                    p.userId(),
                    p.shareAmount(),
                    p.splitType()
            )).toList();

    return new ExpenseRequestDto(
            req.groupId(),
            req.paidBy(),
            req.amount(),
            req.description(),
            req.expenseDate(),
            participants
    );
  }

  public static ExpenseResponse toWeb(ExpenseResponseDto dto) {

    List<ExpenseParticipantResponse> participants = dto.participants().stream()
            .map(p -> new ExpenseParticipantResponse(
                    p.expenseParticipantId(),
                    p.userId(),
                    p.userName(),
                    p.shareAmount(),
                    p.splitType()
            )).toList();

    return new ExpenseResponse(
            dto.expenseId(),
            dto.groupId(),
            dto.paidBy(),
            dto.paidByName(),
            dto.amount(),
            dto.description(),
            dto.expenseDate(),
            dto.createdAt().toString(),
            participants
    );
  }
}
