package com.example.backend.application.service;

import com.example.backend.application.dto.user.*;
import com.example.backend.application.mapper.UserMapper;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.web.dto.user.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService implements UserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;

  @Override
  @Transactional(readOnly = true)
  public Optional<UserResponseDto> getUserById(Long id) {
    return userRepository.findById(id).map(UserMapper::toDto);
  }

  @Override
  public UserResponseDto updateUser(Long id, UserUpdateRequest dto) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    UserMapper.updateDomain(user, dto);

    // Encode password if changed
    if (dto.password() != null && !dto.password().isEmpty()) {
      user.setPasswordHash(encoder.encode(dto.password()));
    }

    User updated = userRepository.save(user);
    return UserMapper.toDto(updated);
  }

  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("User not found");
    }
    userRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserResponseDto> getUserByEmail(String email) {
    return userRepository.findByEmail(email).map(UserMapper::toDto);
  }
}
