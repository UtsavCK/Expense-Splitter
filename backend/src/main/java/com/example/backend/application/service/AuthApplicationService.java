package com.example.backend.application.service;

import com.example.backend.application.dto.auth.AuthResponseDto;
import com.example.backend.application.dto.auth.LoginRequestDto;
import com.example.backend.application.dto.auth.RegisterRequestDto;
import com.example.backend.application.usecase.AuthUseCase;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthApplicationService implements AuthUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  public AuthResponseDto login(LoginRequestDto request) {
    User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid email or password");
    }

    String token = jwtService.generateToken(user.getUserId(), user.getEmail());

    return new AuthResponseDto(
            token,
            user.getUserId(),
            user.getEmail(),
            user.getName()
    );
  }

  @Override
  public AuthResponseDto register(RegisterRequestDto request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email already registered");
    }

    User user = User.builder()
            .name(request.name())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .build();

    User savedUser = userRepository.save(user);

    String token = jwtService.generateToken(savedUser.getUserId(), savedUser.getEmail());

    return new AuthResponseDto(
            token,
            savedUser.getUserId(),
            savedUser.getEmail(),
            savedUser.getName()
    );
  }
}
