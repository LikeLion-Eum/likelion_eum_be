package com.team.startupmatching.controller;


import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.ai.service.AiUserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/users")
public class AiUserSyncController {

    private final AiUserSyncService service;

    // 수동 단건 동기화
    @PostMapping("/{id}/sync")
    public ResponseEntity<Void> syncOne(@PathVariable Long id) {
        service.syncOne(id);
        return ResponseEntity.ok().build();
    }

    // 수동 배치 동기화: { "ids": [1,2,3] }
    @PostMapping("/sync-batch")
    public ResponseEntity<Void> syncBatch(@RequestBody IdsRequest req) {
        service.syncMany(req.ids());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AiUserSnapshot>> listAllForAi() {
        return ResponseEntity.ok(service.listAllSnapshots());
    }

    public record IdsRequest(List<Long> ids) { }
}