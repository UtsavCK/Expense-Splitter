package com.example.backend.application.service;

import com.example.backend.application.dto.group.GroupRequestDto;
import com.example.backend.application.dto.group.GroupResponseDto;
import com.example.backend.application.mapper.GroupMapper;
import com.example.backend.application.usecase.GroupUseCase;
import com.example.backend.domain.model.group.Group;
import com.example.backend.domain.model.group.GroupMember;
import com.example.backend.domain.repository.GroupMemberRepository;
import com.example.backend.domain.repository.GroupRepository;
import com.example.backend.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupApplicationService implements GroupUseCase {

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final GroupMemberRepository gmRepository;

  public GroupApplicationService (
          GroupRepository groupRepository,
          UserRepository userRepository,
          GroupMemberRepository groupMemberRepository
  ) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.gmRepository = groupMemberRepository;
  }

  @Override
  public GroupResponseDto createGroup(GroupRequestDto dto) {
    var creator = userRepository.findById(dto.createdByUserId())
            .orElseThrow(() -> new IllegalArgumentException("Creator not found."));

    Group domaingroup = GroupMapper.toDomain(dto, creator);
    var saved = groupRepository.save(domaingroup);
    GroupMember gm = GroupMember.builder()
            .group(saved)
            .user(creator)
            .build();
    gmRepository.save(gm);
    return GroupMapper.toDto(saved);
  }

  @Override
  public List<GroupResponseDto> getAllGroups() {
    return groupRepository.findAll()
            .stream()
            .map(GroupMapper::toDto)
            .toList();
  }

  @Override
  public Optional<GroupResponseDto> getGroupById(Long id) {
    return groupRepository.findById(id).map(GroupMapper::toDto);
  }
}
