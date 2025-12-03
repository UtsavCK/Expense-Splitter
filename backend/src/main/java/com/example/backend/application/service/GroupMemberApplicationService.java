package com.example.backend.application.service;

import com.example.backend.application.dto.group.GroupMemberCreateDto;
import com.example.backend.application.dto.group.GroupMemberResponseDto;
import com.example.backend.application.mapper.GroupMemberMapper;
import com.example.backend.application.usecase.GroupMemberUseCase;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.ExpenseRepository;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberApplicationService implements GroupMemberUseCase {

  private final GroupMemberRepository groupMemberRepo;
  private final GroupRepository groupRepo;
  private final UserRepository userRepo;
  private final ExpenseRepository expenseRepo;

  @Override
  public GroupMemberResponseDto addMember(GroupMemberCreateDto dto) {
    // Check if already a member
    if (groupMemberRepo.existsByGroupIdAndUserId(dto.groupId(), dto.userId())) {
      throw new IllegalArgumentException("User is already a member of this group!");
    }

    // Validate group and user exist
    Group group = groupRepo.findById(dto.groupId())
            .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    User user = userRepo.findById(dto.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Create membership
    GroupMember gm = GroupMember.builder()
            .group(group)
            .user(user)
            .joinedAt(LocalDateTime.now())
            .build();

    GroupMember saved = groupMemberRepo.save(gm);

    return GroupMemberMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<GroupMemberResponseDto> getMembers(Long groupId) {
    // Validate group exists
    groupRepo.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    return groupMemberRepo.findByGroupId(groupId).stream()
            .map(GroupMemberMapper::toDto)
            .toList();
  }

  @Override
  public void removeMember(Long groupId, Long userId) {
    // Validate group exists
    groupRepo.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    // Find member
    List<GroupMember> members = groupMemberRepo.findByGroupId(groupId);
    GroupMember toRemove = members.stream()
            .filter(m -> m.getUser().getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("User is not a member of this group"));

    // Check if user has any expenses in this group
    long expenseCount = expenseRepo.findByGroupId(groupId).stream()
            .filter(e -> e.getPaidBy().getUserId().equals(userId))
            .count();

    if (expenseCount > 0) {
      throw new IllegalStateException(
              "Cannot remove member who has recorded expenses. Please settle all debts first."
      );
    }

    // Remove membership
    groupMemberRepo.delete(toRemove);
  }
}
