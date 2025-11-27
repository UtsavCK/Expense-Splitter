package com.example.backend.application.service;

import com.example.backend.application.dto.user.*;
import com.example.backend.application.mapper.UserMapper;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.web.dto.user.UserUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserApplicationService implements UserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder;

  public UserApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.encoder = passwordEncoder;
  }

  @Override
  public UserResponseDto createUser(UserRequestDto dto) {
    User user = UserMapper.toDomain(dto);
    user.setPasswordHash(encoder.encode(dto.password()));
    User saved = userRepository.save(user);

    return UserMapper.toDto(saved);
  }

  @Override
  public Optional<UserResponseDto> getUserById(Long id) {
    return userRepository.findById(id).map(UserMapper::toDto);
  }

  @Override
  public List<UserResponseDto> getAllUsers() {
    return userRepository.findAll()
            .stream()
            .map(UserMapper::toDto)
            .toList();
  }

  @Override
  public UserResponseDto updateUser(Long id, UserUpdateRequest dto) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

    UserMapper.updateDomain(user, dto);

    // encode password if changed
    if (dto.password() != null) {
      user.setPasswordHash(encoder.encode(dto.password()));
    }

    User updated = userRepository.save(user);
    return UserMapper.toDto(updated);
  }


  @Override
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id))
      throw new RuntimeException("User not found");
    userRepository.deleteById(id);
  }
}
