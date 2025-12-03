package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.infrastructure.persistence.entity.UserEntity;
import com.example.backend.infrastructure.persistence.mapper.UserEntityMapper;
import com.example.backend.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {
  private final UserJpaRepository jpaRepo;

  public UserRepositoryAdapter (UserJpaRepository userJpaRepository) {
    this.jpaRepo = userJpaRepository;
  }

  @Override
  public User save(User user) {
    UserEntity saved = jpaRepo.save(UserEntityMapper.toEntity(user));
    return UserEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<User> findById(Long id) {
    return jpaRepo.findById(id).map(UserEntityMapper::toDomain);
  }

  @Override
  public List<User> findAll() {
    return jpaRepo.findAll()
            .stream()
            .map(UserEntityMapper::toDomain)
            .toList();
  }

  public Optional<User> findByEmail(String email) {
    return jpaRepo.findByEmail(email).map(UserEntityMapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepo.existsByEmail(email);
  }

  @Override
  public void deleteById(Long id) {

  }

  @Override
  public boolean existsById(Long id) {
    return jpaRepo.existsById(id);
  }

  @Override
  public List<User> findByNameContainingIgnoreCase(String name) {
    return jpaRepo.findByNameContainingIgnoreCase(name).stream()
            .map(UserEntityMapper::toDomain)
            .toList();
  }
}
