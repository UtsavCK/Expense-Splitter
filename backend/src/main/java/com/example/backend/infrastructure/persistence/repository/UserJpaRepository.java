package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByName(String name);
  Optional<UserEntity> findByEmail(String email);
  boolean existsByEmail(String email);
  List<UserEntity> findByNameContainingIgnoreCase(String name);
}
