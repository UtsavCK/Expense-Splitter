package com.example.backend.infrastructure.persistence.adapter;

import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.infrastructure.persistence.entity.GroupMemberEntity;
import com.example.backend.infrastructure.persistence.mapper.GroupMemberEntityMapper;
import com.example.backend.infrastructure.persistence.repository.GroupMemberJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupMemberRepositoryAdapter implements GroupMemberRepository {
  private final GroupMemberJpaRepository jpaRepo;

  public GroupMemberRepositoryAdapter(GroupMemberJpaRepository groupMemberJpaRepository) {
    this.jpaRepo = groupMemberJpaRepository;
  }

  @Override
  public GroupMember save(GroupMember groupMember) {
    GroupMemberEntity saved = jpaRepo.save(GroupMemberEntityMapper.toEntity(groupMember));
    return GroupMemberEntityMapper.toDomain(saved);
  }

  @Override
  public List<GroupMember> findByGroupId(Long groupId) {
    return jpaRepo.findByGroup_GroupId(groupId)
            .stream()
            .map(GroupMemberEntityMapper::toDomain)
            .toList();
  }

  @Override
  public List<GroupMember> findByUserId(Long userId) {
    return jpaRepo.findByUser_UserId(userId).stream()
            .map(GroupMemberEntityMapper::toDomain)
            .toList();
  }

  @Override
  public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
    return jpaRepo.existsByGroup_GroupIdAndUser_UserId(groupId, userId);
  }

  @Override
  public void delete(GroupMember groupMember) {
    jpaRepo.delete(GroupMemberEntityMapper.toEntity(groupMember));
  }
}
