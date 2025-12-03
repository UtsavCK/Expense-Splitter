package com.example.backend.domain.repository;

import com.example.backend.domain.model.user.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository {
  User save(User user);
  Optional<User> findById(Long id);
  Optional<User> findByEmail(String email);
  List<User> findAll();
  boolean existsByEmail(String email);
  void deleteById(Long id);
  boolean existsById(Long id);
  List<User> findByNameContainingIgnoreCase(String name);
}
