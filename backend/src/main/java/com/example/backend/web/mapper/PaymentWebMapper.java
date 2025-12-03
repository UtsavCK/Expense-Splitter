package com.example.backend.web.mapper;

import com.example.backend.application.dto.payment.PaymentRequestDto;
import com.example.backend.application.dto.payment.PaymentResponseDto;
import com.example.backend.web.dto.payment.PaymentCreateRequest;
import com.example.backend.web.dto.payment.PaymentResponse;

public class PaymentWebMapper {

  public static PaymentRequestDto toApplication(PaymentCreateRequest req) {
    return new PaymentRequestDto(
            req.paidBy(),
            req.paidTo(),
            req.groupId(),
            req.amount(),
            req.paymentDate(),
            req.notes()
    );
  }

  public static PaymentResponse toWeb(PaymentResponseDto dto) {
    return new PaymentResponse(
            dto.paymentId(),
            dto.paidBy(),
            dto.paidByName(),
            dto.paidTo(),
            dto.paidToName(),
            dto.groupId(),
            dto.groupName(),
            dto.amount(),
            dto.paymentDate(),
            dto.notes()
    );
  }
}
