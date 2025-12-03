package com.example.backend.domain.repository;

import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
  Group save(Group group);
  Optional<Group> findById(Long id);
  List<Group> findByCreatedBy(Long userId);
  List<Group> findAll();
//  Boolean hasMember(User user);
}
