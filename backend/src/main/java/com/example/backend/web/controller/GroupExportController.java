package com.example.backend.web.controller;

import com.example.backend.application.dto.balance.BalanceDto;
import com.example.backend.application.dto.balance.GroupBalanceSummaryDto;
import com.example.backend.application.dto.expense.ExpenseResponseDto;
import com.example.backend.application.usecase.BalanceUseCase;
import com.example.backend.application.usecase.ExpenseUseCase;
import com.example.backend.application.usecase.PaymentUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups/{groupId}/export")
@RequiredArgsConstructor
public class GroupExportController {
  private final BalanceUseCase balanceUseCase;
  private final ExpenseUseCase expenseUseCase;
  private final PaymentUseCase paymentUseCase;
  private final AuthorizationDomainService authService;

  // FIX 1: Return byte array instead of String for proper file handling
  // FIX 2: Add proper charset encoding (UTF-8)
  // FIX 3: Use application/octet-stream for better download handling
  @GetMapping("/balances.csv")
  public ResponseEntity<byte[]> exportBalancesAsCSV(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);

    GroupBalanceSummaryDto balanceSummary = balanceUseCase.calculateGroupBalances(groupId);
    StringBuilder csv = new StringBuilder();

    // FIX 4: Add BOM for Excel UTF-8 compatibility
    csv.append("\uFEFF"); // UTF-8 BOM
    csv.append("From User,To User,Amount,Description\n");

    for (BalanceDto balance : balanceSummary.balances()) {
      csv.append(String.format("\"%s\",\"%s\",%.2f,\"Balance\"\n",
              escapeCSV(balance.fromUserName()),        // FIX 5: Escape special chars
              escapeCSV(balance.toUserName()),
              balance.amount()
      ));
    }

    byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"balances_" + groupId + ".csv\"; filename*=UTF-8''balances_" + groupId + ".csv")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(csvBytes.length))
            .body(csvBytes);
  }

  @GetMapping("/expenses.csv")
  public ResponseEntity<byte[]> exportExpensesAsCSV(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);

    List<ExpenseResponseDto> expenses = expenseUseCase.getExpensesByGroup(groupId);
    StringBuilder csv = new StringBuilder();

    csv.append("\uFEFF"); // UTF-8 BOM
    csv.append("Date,Description,Amount,Paid By,Participants\n");

    for (ExpenseResponseDto expense : expenses) {
      // FIX 6: Add null/empty checks
      String participants = expense.participants() != null && !expense.participants().isEmpty()
              ? expense.participants().stream()
              .map(p -> escapeCSV(p.userName()))
              .collect(Collectors.joining("; "))
              : "N/A";

      csv.append(String.format("\"%s\",\"%s\",%.2f,\"%s\",\"%s\"\n",
              expense.expenseDate() != null ? expense.expenseDate().toString() : "N/A",
              escapeCSV(expense.description()),
              expense.amount() != null ? expense.amount() : BigDecimal.ZERO,
              escapeCSV(expense.paidByName()),
              participants
      ));
    }

    byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"expenses_" + groupId + ".csv\"; filename*=UTF-8''expenses_" + groupId + ".csv")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(csvBytes.length))
            .body(csvBytes);
  }

  @GetMapping("/summary.csv")
  public ResponseEntity<byte[]> exportSummaryAsCSV(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);

    GroupBalanceSummaryDto balanceSummary = balanceUseCase.calculateGroupBalances(groupId);
    List<ExpenseResponseDto> expenses = expenseUseCase.getExpensesByGroup(groupId);

    StringBuilder csv = new StringBuilder();
    csv.append("\uFEFF"); // UTF-8 BOM

    // FIX 7: Proper CSV formatting for report section
    csv.append("GROUP SUMMARY REPORT\n");
    csv.append("Group Name,").append(escapeCSV(balanceSummary.groupName())).append("\n");
    csv.append("Total Expenses,").append(expenses.size()).append("\n");

    BigDecimal totalAmount = expenses.stream()
            .map(ExpenseResponseDto::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    csv.append("Total Amount,").append(String.format("%.2f", totalAmount)).append("\n");
    csv.append("\n");

    csv.append("CURRENT BALANCES\n");
    csv.append("From User,To User,Amount\n");

    for (BalanceDto balance : balanceSummary.balances()) {
      csv.append(String.format("\"%s\",\"%s\",%.2f\n",
              escapeCSV(balance.fromUserName()),
              escapeCSV(balance.toUserName()),
              balance.amount()
      ));
    }

    byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"summary_" + groupId + ".csv\"; filename*=UTF-8''summary_" + groupId + ".csv")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(csvBytes.length))
            .body(csvBytes);
  }

  // FIX 8: Helper method to escape CSV special characters
  private String escapeCSV(String value) {
    if (value == null) {
      return "";
    }

    // If contains comma, newline, or quote, wrap in quotes and escape inner quotes
    if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }
}