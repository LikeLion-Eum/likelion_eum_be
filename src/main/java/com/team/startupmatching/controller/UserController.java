// src/main/java/com/team/startupmatching/controller/UserController.java
package com.team.startupmatching.controller;

import com.team.startupmatching.dto.UserCreateRequest;
import com.team.startupmatching.dto.UserPatchRequest;
import com.team.startupmatching.dto.UserResponse;
import com.team.startupmatching.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    // 생성
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest req) {
        UserResponse body = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // 부분 수정
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patch(@PathVariable long id,
                                              @RequestBody UserPatchRequest req) {
        UserResponse body = userService.patch(id, req);
        return ResponseEntity.ok(body);
    }

    // 🔍 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getOne(@PathVariable long id) {
        UserResponse body = userService.getOne(id);
        return ResponseEntity.ok(body);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> body = userService.getAll();
        return ResponseEntity.ok(body);
    }


}
