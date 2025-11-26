//package com.example.backend.web.controller;
//
//import com.example.backend.application.usecase.ExpenseParticipantUseCases;
//import com.example.backend.domain.model.expense.ExpenseParticipant;
//import com.example.backend.web.dto.ExpenseParticipantRequest;
//import com.example.backend.web.dto.ExpenseParticipantResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/expense-participants")
//public class ExpenseParticipantController {
//
//  private final ExpenseParticipantUseCases useCases;
//
//  public ExpenseParticipantController(ExpenseParticipantUseCases useCases) {
//    this.useCases = useCases;
//  }
//
//  @PostMapping
//  public ResponseEntity<ExpenseParticipantResponse> addParticipant(@RequestBody ExpenseParticipantRequest req) {
//    ExpenseParticipant ep = useCases.addParticipant(req.toDomain());
//    return ResponseEntity.ok(ExpenseParticipantResponse.fromDomain(ep));
//  }
//
//  @GetMapping("/expense/{expenseId}")
//  public List<ExpenseParticipantResponse> listByExpense(@PathVariable Long expenseId) {
//    return useCases.getParticipantsOfExpense(expenseId)
//            .stream()
//            .map(ExpenseParticipantResponse::fromDomain)
//            .toList();
//  }
//}
