package com.example.backend.application.service;

import com.example.backend.application.dto.auth.*;
import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.usecase.AuthUseCase;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.infrastructure.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService implements AuthUseCase {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;
  private final JwtProvider jwt;

  public AuthApplicationService(UserRepository userRepo, PasswordEncoder encoder, JwtProvider jwt) {
    this.userRepo = userRepo;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  @Override
  public LoginResponseDto login(LoginRequestDto req) {
    var user = userRepo.findByEmail(req.email())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

    if (!encoder.matches(req.password(), user.getPasswordHash()))
      throw new RuntimeException("Invalid credentials");

    String token = jwt.generateToken(user.getUserId(), user.getEmail());

    return new LoginResponseDto(
            token,
            user.getUserId(),
            user.getEmail()
    );
  }

  public UserResponseDto register(UserRequestDto req) {
    // Save user with encoded password
    User user = new User();
    user.setName(req.name());
    user.setEmail(req.email());
    user.setPasswordHash(encoder.encode(req.password()));
    userRepo.save(user);
    return new UserResponseDto(user.getUserId(), user.getEmail(), user.getName(), user.getCreatedAt());
  }
}
