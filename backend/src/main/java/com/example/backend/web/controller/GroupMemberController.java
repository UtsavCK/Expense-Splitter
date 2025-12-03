package com.example.backend.web.controller;

import com.example.backend.application.dto.group.GroupMemberCreateDto;
import com.example.backend.application.dto.user.UserResponseDto;
import com.example.backend.application.usecase.GroupMemberUseCase;
import com.example.backend.application.usecase.UserUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.dto.group.GroupMemberAddByEmailRequest;
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

  private final UserUseCase userService;
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

  @PostMapping("/by-email")
  public ResponseEntity<GroupMemberResponse> addMemberByEmail(
          @PathVariable Long groupId,
          @RequestBody GroupMemberAddByEmailRequest request,
          @CurrentUser Long currentUserId
  ) {
    authService.requireGroupAccess(currentUserId, groupId, "add members");

    // Find user by email
    UserResponseDto user = userService.getUserByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException(
                    "No user found with email: " + request.email()
            ));

    // Add as member
    var appDto = new GroupMemberCreateDto(groupId, user.userId());
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
