package com.example.backend.application.service;

import com.example.backend.application.dto.group.GroupRequestDto;
import com.example.backend.application.dto.group.GroupResponseDto;
import com.example.backend.application.mapper.GroupMapper;
import com.example.backend.application.usecase.GroupUseCase;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.model.user.User;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupApplicationService implements GroupUseCase {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository groupMemberRepository;

  @Override
  public GroupResponseDto createGroup(GroupRequestDto dto) {
    User creator = userRepository.findById(dto.createdByUserId())
            .orElseThrow(() -> new IllegalArgumentException("Creator not found."));

    Group domainGroup = GroupMapper.toDomain(dto, creator);
    Group saved = groupRepository.save(domainGroup);

    // Automatically add creator as first member
    GroupMember gm = GroupMember.builder()
            .group(saved)
            .user(creator)
            .joinedAt(LocalDateTime.now())
            .build();
    groupMemberRepository.save(gm);

    return GroupMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<GroupResponseDto> getUserGroups(Long userId) {
    List<GroupMember> memberships = groupMemberRepository.findByUserId(userId);
    return memberships.stream()
            .map(gm -> GroupMapper.toDto(gm.getGroup()))
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<GroupResponseDto> getGroupById(Long id) {
    return groupRepository.findById(id).map(GroupMapper::toDto);
  }
}