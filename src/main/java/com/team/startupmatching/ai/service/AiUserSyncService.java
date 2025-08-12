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
    private final AiClient aiClient;

    /** DB에 있는 userId를 조회해서 AI로 업서트 */
    @Transactional(readOnly = true)
    public long syncOne(long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        AiUserSnapshot snap = AiUserSnapshotMapper.from(u); // id, name, career, introduction, skills(list), location, resumUrl
        aiClient.upsertUsers(List.of(snap));
        return u.getId();
    }
}