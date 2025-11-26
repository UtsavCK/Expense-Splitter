package com.example.backend.application.service;

import com.example.backend.application.dto.user.UserRequestDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.mapper.UserMapper;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserApplicationService implements UserUseCase {

  private final UserRepository userRepository;

  public UserApplicationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserResponseDto createUser(UserRequestDto dto) {
    User domainUser = UserMapper.toDomain(dto);
    //password hashing here
    var saved = userRepository.save(domainUser);
    return UserMapper.toDto(saved);
  }

  @Override
  public Optional<UserResponseDto> getUserById(Long id) {
    return userRepository.findById(id).map(UserMapper::toDto);
  }
}
