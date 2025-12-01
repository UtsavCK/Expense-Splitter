package com.example.backend.domain.service;

import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.infrastructure.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationDomainService {

  private final GroupMemberRepository groupMemberRepository;

  public boolean isGroupMember(Long userId, Long groupId) {
    return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
  }

  public void requireGroupMembership(Long userId, Long groupId) {
    if (!isGroupMember(userId, groupId)) {
      throw new UnauthorizedException(
              String.format("User %d is not a member of group %d", userId, groupId)
      );
    }
  }

  public void requireGroupAccess(Long userId, Long groupId, String action) {
    if (!isGroupMember(userId, groupId)) {
      throw new UnauthorizedException(
              String.format("User %d cannot %s in group %d - not a member", userId, action, groupId)
      );
    }
  }
}