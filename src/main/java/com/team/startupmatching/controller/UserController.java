package com.team.startupmatching.controller;


import com.team.startupmatching.dto.UserCreateRequest;
import com.team.startupmatching.dto.UserPatchRequest;
import com.team.startupmatching.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    // 생성
    @PostMapping
    public ResponseEntity<IdRes> create(@Valid @RequestBody UserCreateRequest req) {
        Long id = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdRes(id));
    }

    // 부분 수정 (null인 필드는 미변경)
    @PatchMapping("/{id}")
    public ResponseEntity<IdRes> patch(@PathVariable long id,
                                       @RequestBody UserPatchRequest req) {
        Long updated = userService.patch(id, req);
        return ResponseEntity.ok(new IdRes(updated));
    }

    private record IdRes(Long id) {}
}
