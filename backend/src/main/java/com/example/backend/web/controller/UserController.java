package com.example.backend.web.controller;

import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.dto.user.UserSearchResultDto;
import com.example.backend.application.dto.user.UserStatsDto;
import com.example.backend.application.dto.user.UserUpdateDto;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.web.dto.user.*;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserUseCase userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getMyProfile(@CurrentUser Long userId) {
    return userService.getUserById(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/me")
  public ResponseEntity<UserResponseDto> updateMyProfile(
          @CurrentUser Long userId,
          @RequestBody UserUpdateRequest request
  ) {
    var updateDto = new UserUpdateDto(
            request.name(),
            null,
            request.currentPassword(),
            request.newPassword()
    );

    try {
      var updated = userService.updateUser(userId, updateDto);
      return ResponseEntity.ok(updated);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
              .header("X-Error-Message", e.getMessage())
              .build();
    }
  }

  @DeleteMapping("/me")
  public ResponseEntity<?> deleteMyAccount(@CurrentUser Long userId) {
    try {
      userService.deleteUser(userId);
      return ResponseEntity.noContent().build();
    } catch (IllegalStateException e) {
      // Return 409 Conflict when user has balances or unsettled groups
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(Map.of(
                      "error", "Cannot Delete Account",
                      "message", e.getMessage()
              ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
              .body(Map.of(
                      "error", "Bad Request",
                      "message", e.getMessage()
              ));
    }
  }

  @GetMapping("/search")
  public ResponseEntity<List<UserSearchResultDto>> searchUsers(
          @RequestParam String query,
          @CurrentUser Long userId
  ) {
    List<UserSearchResultDto> results = userService.searchUsers(query);
    return ResponseEntity.ok(results);
  }

  @GetMapping("/me/stats")
  public ResponseEntity<UserStatsDto> getMyStats(@CurrentUser Long userId) {
    UserStatsDto stats = userService.getUserStats(userId);
    return ResponseEntity.ok(stats);
  }

  // ADD THIS: New endpoint to check deletion eligibility
  @GetMapping("/me/can-delete")
  public ResponseEntity<DeletionEligibilityDto> canDeleteAccount(@CurrentUser Long userId) {
    try {
      userService.deleteUser(userId); // This will throw if not eligible
      // If no exception, user can delete (but we didn't actually delete)
      return ResponseEntity.ok(new DeletionEligibilityDto(
              true,
              "Your account can be deleted",
              null
      ));
    } catch (IllegalStateException e) {
      // User has balances or unsettled groups
      return ResponseEntity.ok(new DeletionEligibilityDto(
              false,
              "Your account cannot be deleted",
              e.getMessage()
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.ok(new DeletionEligibilityDto(
              false,
              "Your account cannot be deleted",
              e.getMessage()
      ));
    }
  }
}
