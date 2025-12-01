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
    // Validate creator exists
    User creator = userRepository.findById(dto.createdByUserId())
            .orElseThrow(() -> new IllegalArgumentException("Creator user not found."));

    // Create group
    Group domainGroup = GroupMapper.toDomain(dto, creator);
    Group saved = groupRepository.save(domainGroup);

    // Automatically add creator as first member
    GroupMember creatorMembership = GroupMember.builder()
            .group(saved)
            .user(creator)
            .build();
    groupMemberRepository.save(creatorMembership);

    return GroupMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<GroupResponseDto> getAllGroups() {
    return groupRepository.findAll().stream()
            .map(GroupMapper::toDto)
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<GroupResponseDto> getGroupById(Long id) {
    return groupRepository.findById(id)
            .map(GroupMapper::toDto);
  }
}
