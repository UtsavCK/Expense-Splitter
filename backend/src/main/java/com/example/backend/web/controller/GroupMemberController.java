package com.example.backend.web.controller;

import com.example.backend.application.usecase.GroupMemberUseCase;
import com.example.backend.web.dto.group.GroupMemberAddRequest;
import com.example.backend.web.dto.group.GroupMemberResponse;
import com.example.backend.web.mapper.GroupMemberWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMemberController {

  private final GroupMemberUseCase groupMemberService;

  // ADD MEMBER
  @PostMapping
  public ResponseEntity<GroupMemberResponse> addMember(
          @PathVariable Long groupId,
          @RequestBody GroupMemberAddRequest request
  ) {
    var appDto = GroupMemberWebMapper.toApp(groupId, request);
    var result = groupMemberService.addMember(appDto);
    return ResponseEntity.ok(GroupMemberWebMapper.toWeb(result));
  }

  // LIST MEMBERS
  @GetMapping
  public List<GroupMemberResponse> listMembers(@PathVariable Long groupId) {
    return groupMemberService.getMembers(groupId)
            .stream()
            .map(GroupMemberWebMapper::toWeb)
            .toList();
  }

  // REMOVE MEMBER
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> removeMember(
          @PathVariable Long groupId,
          @PathVariable Long userId
  ) {
    groupMemberService.removeMember(groupId, userId);
    return ResponseEntity.noContent().build();
  }
}
