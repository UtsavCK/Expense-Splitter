package com.example.backend.domain.repository;

import com.example.backend.domain.model.group.GroupMember;

import java.util.List;

public interface GroupMemberRepository {
  GroupMember save(GroupMember groupMember);
  List<GroupMember> findByGroupId(Long groupId);
  boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
