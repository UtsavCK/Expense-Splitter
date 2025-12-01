package com.example.backend.web.controller;

import com.example.backend.application.usecase.GroupMemberUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.dto.group.GroupMemberAddRequest;
import com.example.backend.web.dto.group.GroupMemberResponse;
import com.example.backend.web.mapper.GroupMemberWebMapper;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMemberController {

  private final GroupMemberUseCase groupMemberService;
  private final AuthorizationDomainService authService;

  @PostMapping
  public ResponseEntity<GroupMemberResponse> addMember(
          @PathVariable Long groupId,
          @RequestBody GroupMemberAddRequest request,
          @CurrentUser Long currentUserId
  ) {
    // Check if current user is a group member
    authService.requireGroupAccess(currentUserId, groupId, "add members");

    var appDto = GroupMemberWebMapper.toApp(groupId, request);
    var result = groupMemberService.addMember(appDto);
    return ResponseEntity.ok(GroupMemberWebMapper.toWeb(result));
  }

  @GetMapping
  public List<GroupMemberResponse> listMembers(
          @PathVariable Long groupId,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, groupId);
    return groupMemberService.getMembers(groupId).stream()
            .map(GroupMemberWebMapper::toWeb)
            .toList();
  }

  @DeleteMapping("/{userIdToRemove}")
  public ResponseEntity<Void> removeMember(
          @PathVariable Long groupId,
          @PathVariable Long userIdToRemove,
          @CurrentUser Long currentUserId
  ) {
    // User can remove themselves OR must be a group member to remove others
    if (!currentUserId.equals(userIdToRemove)) {
      authService.requireGroupAccess(currentUserId, groupId, "remove members");
    }

    groupMemberService.removeMember(groupId, userIdToRemove);
    return ResponseEntity.noContent().build();
  }
}
