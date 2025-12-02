package com.example.backend.web.controller;

import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.web.dto.user.*;
import com.example.backend.web.mapper.UserWebMapper;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserUseCase userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMyProfile(@CurrentUser Long userId) {
    return userService.getUserById(userId)
            .map(UserWebMapper::toWeb)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/me")
  public ResponseEntity<UserResponse> updateMyProfile(
          @CurrentUser Long userId,
          @RequestBody UserUpdateRequest request
  ) {
    var updated = userService.updateUser(userId, request);
    return ResponseEntity.ok(UserWebMapper.toWeb(updated));
  }

  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMyAccount(@CurrentUser Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }
}