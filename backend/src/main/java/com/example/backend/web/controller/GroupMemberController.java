//package com.example.backend.web.controller;
//
//import com.example.backend.application.usecase.GroupMemberUseCases;
//import com.example.backend.domain.model.group.GroupMember;
//import com.example.backend.web.dto.GroupMemberRequest;
//import com.example.backend.web.dto.GroupMemberResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/group-members")
//public class GroupMemberController {
//
//  private final GroupMemberUseCases useCases;
//
//  public GroupMemberController(GroupMemberUseCases useCases) {
//    this.useCases = useCases;
//  }
//
//  @PostMapping
//  public ResponseEntity<GroupMemberResponse> addMember(@RequestBody GroupMemberRequest req) {
//    GroupMember gm = useCases.addMember(req.toDomain());
//    return ResponseEntity.ok(GroupMemberResponse.fromDomain(gm));
//  }
//
//  @GetMapping("/group/{groupId}")
//  public List<GroupMemberResponse> getByGroup(@PathVariable Long groupId) {
//    return useCases.getMembersOfGroup(groupId)
//            .stream()
//            .map(GroupMemberResponse::fromDomain)
//            .toList();
//  }
//}
