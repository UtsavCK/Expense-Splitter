package com.example.backend.domain.repository;

import com.example.backend.domain.model.group.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
  Group save(Group group);
  Optional<Group> findById(Long id);
  List<Group> findByCreatedBy(Long userId);
  List<Group> findAll();
}
