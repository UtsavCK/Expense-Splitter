package com.example.backend.web.controller;

import com.example.backend.application.usecase.GroupUseCase;
import com.example.backend.web.dto.group.*;
import com.example.backend.web.mapper.GroupWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final GroupUseCase groupService;

  public GroupController(GroupUseCase groupUseCase) {
    this.groupService = groupUseCase;
  }

  @PostMapping
  public ResponseEntity<GroupResponse> create(
          @RequestBody GroupCreateRequest req,
          @RequestHeader("X-User-Id") Long creatorId
  ) {
    var appDto = GroupWebMapper.toApplication(req, creatorId);
    var result = groupService.createGroup(appDto);
    return ResponseEntity.ok(GroupWebMapper.toWeb(result));
  }

  @GetMapping
  public List<GroupResponse> getAllUsers() {
    return groupService.getAllGroups()
            .stream()
            .map(GroupWebMapper::toWeb)
            .toList();
  }
}
