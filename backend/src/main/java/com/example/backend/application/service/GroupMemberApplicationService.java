package com.example.backend.application.service;

import com.example.backend.application.dto.group.GroupMemberCreateDto;
import com.example.backend.application.dto.group.GroupMemberResponseDto;
import com.example.backend.application.mapper.GroupMemberMapper;
import com.example.backend.application.usecase.GroupMemberUseCase;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMemberApplicationService implements GroupMemberUseCase {

  private final GroupMemberRepository groupMemberRepo;
  private final GroupRepository groupRepo;
  private final UserRepository userRepo;

  @Override
  public GroupMemberResponseDto addMember(GroupMemberCreateDto dto) {

    if (groupMemberRepo.existsByGroupIdAndUserId(dto.groupId(), dto.userId())) {
      throw new RuntimeException("User already in group");
    }

    var group = groupRepo.findById(dto.groupId())
            .orElseThrow(() -> new RuntimeException("Group not found"));

    var user = userRepo.findById(dto.userId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    GroupMember gm = GroupMember.builder()
            .group(group)
            .user(user)
            .build();

    var saved = groupMemberRepo.save(gm);

    return GroupMemberMapper.toDto(saved);
  }

  @Override
  public List<GroupMemberResponseDto> getMembers(Long groupId) {
    return groupMemberRepo.findByGroupId(groupId)
            .stream()
            .map(GroupMemberMapper::toDto)
            .toList();
  }

  @Override
  public void removeMember(Long groupId, Long userId) {
    var members = groupMemberRepo.findByGroupId(groupId);

    var toRemove = members.stream()
            .filter(m -> m.getUser().getUserId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("User not in group"));

    groupMemberRepo.delete(toRemove);
  }
}
