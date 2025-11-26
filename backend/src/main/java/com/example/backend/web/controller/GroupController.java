package com.example.backend.web.controller;

import com.example.backend.application.usecase.GroupUseCase;
import com.example.backend.web.dto.group.*;
import com.example.backend.web.mapper.GroupWebMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

  private final GroupUseCase groups;

  public GroupController(GroupUseCase groups) {
    this.groups = groups;
  }

  @PostMapping
  public GroupResponse create(@Valid @RequestBody GroupCreateRequest req) {
    return GroupWebMapper.toWeb(
            groups.createGroup(GroupWebMapper.toApplication(req))
    );
  }
}
