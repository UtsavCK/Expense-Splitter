package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.infrastructure.persistence.entity.GroupEntity;
import com.example.backend.infrastructure.persistence.mapper.GroupEntityMapper;
import com.example.backend.infrastructure.persistence.repository.GroupJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupRepositoryAdapter implements GroupRepository {
  private GroupJpaRepository jpaRepo;

  public GroupRepositoryAdapter(GroupJpaRepository groupJpaRepository) {
    this.jpaRepo = groupJpaRepository;
  }

  @Override
  public Group save(Group group) {
    GroupEntity saved = jpaRepo.save(GroupEntityMapper.toEntity(group));
    return GroupEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<Group> findById(Long id) {
    return jpaRepo.findById(id).map(GroupEntityMapper::toDomain);
  }

  @Override
  public List<Group> findByCreatedBy(Long userId) {
    return jpaRepo.findByCreatedByUserId(userId)
            .stream()
            .map(GroupEntityMapper::toDomain)
            .toList();
  }

  @Override
  public List<Group> findAll() {
    return jpaRepo.findAll()
            .stream()
            .map(GroupEntityMapper::toDomain)
            .toList();
  }
}
