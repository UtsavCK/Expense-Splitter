package com.example.backend.web.controller;

import com.example.backend.application.dto.user.UserUpdateDto;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.web.dto.user.*;
import com.example.backend.web.mapper.UserWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserUseCase userService;

  @PostMapping
  public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
    var appDto = UserWebMapper.toCreateApp(request);
    var result = userService.createUser(appDto);
    return ResponseEntity.ok(UserWebMapper.toWeb(result));
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers()
            .stream()
            .map(UserWebMapper::toWeb)
            .toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return userService.getUserById(id)
            .map(UserWebMapper::toWeb)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
          @PathVariable Long id,
          @RequestBody UserUpdateRequest request
  ) {
    var updated = userService.updateUser(id, request);

    return ResponseEntity.ok(UserWebMapper.toWeb(updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
