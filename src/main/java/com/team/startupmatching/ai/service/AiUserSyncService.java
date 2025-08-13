package com.team.startupmatching.ai.service;

import com.team.startupmatching.ai.client.AiClient;
import com.team.startupmatching.ai.mapper.AiUserSnapshotMapper;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AiUserSyncService {

    private final UserRepository userRepository;
    private final AiUserSnapshotMapper mapper;
    private final AiClient aiClient;

    /** 단건 업서트 (메서드명 변경: upsertOne -> upsertUser) */
    @Transactional(readOnly = true)
    public void syncOne(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        aiClient.upsertUser(mapper.toSnapshot(user));
    }

    /** 배치 업서트 (단건 반복) */
    @Transactional(readOnly = true)
    public void syncBatch(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return;
        for (Long id : userIds) {
            syncOne(id);
        }
    }
}
