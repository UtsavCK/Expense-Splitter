package com.example.backend.infrastructure.persistence.repository;

import com.example.backend.infrastructure.persistence.entity.GroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberJpaRepository extends JpaRepository<GroupMemberEntity, Long> {
  List<GroupMemberEntity> findByGroup_GroupId(Long groupId);
  List<GroupMemberEntity> findByUser_UserId(Long userId);
  boolean existsByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
}
