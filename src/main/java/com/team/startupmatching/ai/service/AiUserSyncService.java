package com.team.startupmatching.ai.service;

import com.team.startupmatching.ai.client.AiClient;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.ai.mapper.AiUserSnapshotMapper;
import com.team.startupmatching.entity.User;
import com.team.startupmatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiUserSyncService {

    private final UserRepository userRepository;
    private final AiUserSnapshotMapper mapper;
    private final AiClient aiClient;

    @Transactional(readOnly = true)
    public void syncOne(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
        aiClient.upsertOne(mapper.toSnapshot(user));
    }

    @Transactional(readOnly = true)
    public void syncMany(List<Long> ids) {
        var snapshots = userRepository.findAllById(ids).stream()
                .map(mapper::toSnapshot)
                .toList();
        aiClient.upsertMany(snapshots);
    }

    @Transactional(readOnly = true)
    public List<AiUserSnapshot> listAllSnapshots() {
        return userRepository.findAll()
                .stream()
                .map(mapper::toSnapshot)   // id,name,career,introduction,skills(String),location,resume_url
                .toList();
    }
}