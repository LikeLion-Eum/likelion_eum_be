package com.team.startupmatching.controller;

import com.team.startupmatching.ai.service.AiIncubationCenterSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/incubation-centers")
public class AiIncubationCenterSyncController {

    private final AiIncubationCenterSyncService syncService;

    /** 단건 수동 업서트 트리거 */
    @PostMapping("/{id}/sync")
    public ResponseEntity<Void> syncOne(@PathVariable Long id) {
        syncService.syncOne(id);
        return ResponseEntity.accepted().build();
    }

    /** 배치 수동 업서트 트리거: { "ids": [1,2,3] } */
    @PostMapping("/sync-batch")
    public ResponseEntity<Void> syncBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = body.getOrDefault("ids", List.of());
        syncService.syncBatch(ids);
        return ResponseEntity.accepted().build();
    }
}
