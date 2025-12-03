package com.example.backend.domain.service;

import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import com.example.backend.infrastructure.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationDomainService {

  private final UserRepository userRepository;
  private final GroupRepository groupRepository;
  private final GroupMemberRepository groupMemberRepository;

  public boolean isGroupMember(Long userId, Long groupId) {
    return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
  }

  public void requireGroupMembership(Long userId, Long groupId) {
    if (!isGroupMember(userId, groupId)) {
      String userName = userRepository.findById(userId)
              .map(User::getName)
              .orElse("User " + userId);
      String groupName = groupRepository.findById(groupId)
              .map(Group::getName)
              .orElse("Group " + groupId);
      throw new UnauthorizedException(
              String.format("%s is not a member of '%s'", userName, groupName)
      );
    }
  }

  public void requireGroupAccess(Long userId, Long groupId, String action) {
    if (!isGroupMember(userId, groupId)) {
      String userName = userRepository.findById(userId)
              .map(User::getName)
              .orElse("User " + userId);
      String groupName = groupRepository.findById(groupId)
              .map(Group::getName)
              .orElse("Group " + groupId);
      throw new UnauthorizedException(
              String.format("%s cannot %s in '%s' - not a member", userName, action, groupName)
      );
    }
  }
}