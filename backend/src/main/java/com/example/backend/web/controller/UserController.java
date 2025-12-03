package com.example.backend.web.controller;

import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.dto.user.UserSearchResultDto;
import com.example.backend.application.dto.user.UserStatsDto;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.web.dto.user.*;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    var updateDto = new UserUpdateRequest(
            request.name(),
            request.email(),
            request.password()
    );
    var updated = userService.updateUser(userId, updateDto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMyAccount(@CurrentUser Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
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
}