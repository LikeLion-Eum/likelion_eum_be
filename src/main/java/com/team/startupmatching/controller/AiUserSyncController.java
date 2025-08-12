package com.team.startupmatching.controller;


import com.team.startupmatching.ai.service.AiUserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ai/users")
public class AiUserSyncController {

    private final AiUserSyncService aiUserSyncService;

    @PostMapping("/{id}/sync")
    public ResponseEntity<?> syncOne(@PathVariable long id) {
        long syncedId = aiUserSyncService.syncOne(id);
        return ResponseEntity.ok(new IdResponse(syncedId));
    }

    private record IdResponse(long id) {}
}