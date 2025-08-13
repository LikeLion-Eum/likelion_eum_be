package com.team.startupmatching.ai.service;

import com.team.startupmatching.ai.client.AiClient;
import com.team.startupmatching.ai.mapper.AiIncubationCenterSnapshotMapper;
import com.team.startupmatching.entity.IncubationCenter;
import com.team.startupmatching.repository.IncubationCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AiIncubationCenterSyncService {

    private final IncubationCenterRepository repository;
    private final AiIncubationCenterSnapshotMapper mapper;
    private final AiClient aiClient;

    /** 단건 업서트 */
    @Transactional(readOnly = true)
    public void syncOne(Long id) {
        IncubationCenter e = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("IncubationCenter not found: " + id));
        aiClient.upsertIncubationCenter(mapper.toSnapshot(e));
    }

    /** 배치 업서트(단건 반복) */
    @Transactional(readOnly = true)
    public void syncBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        for (Long id : ids) {
            syncOne(id);
        }
    }
}
