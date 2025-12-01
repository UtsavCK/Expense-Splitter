package com.example.backend.web.controller;

import com.example.backend.application.usecase.GroupUseCase;
import com.example.backend.domain.service.AuthorizationDomainService;
import com.example.backend.web.dto.group.*;
import com.example.backend.web.mapper.GroupWebMapper;
import com.example.backend.web.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

  private final GroupUseCase groupService;
  private final AuthorizationDomainService authService;

  @PostMapping
  public ResponseEntity<GroupResponse> create(
          @RequestBody GroupCreateRequest req,
          @CurrentUser Long userId
  ) {
    var appDto = GroupWebMapper.toApplication(req, userId);
    var result = groupService.createGroup(appDto);
    return ResponseEntity.ok(GroupWebMapper.toWeb(result));
  }

  @GetMapping("/my-groups")
  public List<GroupResponse> getMyGroups(@CurrentUser Long userId) {
    // TODO: Implement getUserGroups in GroupUseCase
    return groupService.getUserGroups(userId).stream()
            .map(GroupWebMapper::toWeb)
            .toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<GroupResponse> getGroup(
          @PathVariable Long id,
          @CurrentUser Long userId
  ) {
    authService.requireGroupMembership(userId, id);
    return groupService.getGroupById(id)
            .map(GroupWebMapper::toWeb)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }
}
