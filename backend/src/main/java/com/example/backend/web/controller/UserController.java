package com.example.backend.web.controller;

import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.web.dto.user.*;
import com.example.backend.web.mapper.UserWebMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserUseCase users;

  public UserController(UserUseCase users) {
    this.users = users;
  }

  @PostMapping("/register")
  public UserResponse register(@Valid @RequestBody UserCreateRequest req) {
    return UserWebMapper.toWeb(
            users.createUser(UserWebMapper.toApplication(req))
    );
  }
}
