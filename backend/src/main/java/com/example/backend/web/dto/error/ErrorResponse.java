package com.example.backend.web.dto.error;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
  private int status;
  private String message;
  private LocalDateTime timestamp;
  private String path;
}