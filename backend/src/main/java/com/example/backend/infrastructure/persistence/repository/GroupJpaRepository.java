package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.domain.model.group.Group;
import com.example.backend.infrastructure.persistence.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupJpaRepository extends JpaRepository<GroupEntity, Long> {
  List<GroupEntity> findByCreatedByUserId(Long userId);
}
