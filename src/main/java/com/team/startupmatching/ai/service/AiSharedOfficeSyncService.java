package com.team.startupmatching.ai.service;

import com.team.startupmatching.ai.client.AiClient;
import com.team.startupmatching.ai.dto.AiSharedOfficeSnapshot;
import com.team.startupmatching.ai.mapper.AiSharedOfficeSnapshotMapper;
import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.repository.SharedOfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiSharedOfficeSyncService {

    private final SharedOfficeRepository sharedOfficeRepository;
    private final AiClient aiClient;

    /** DB에 있는 sharedOfficeId를 조회해서 AI로 업서트 */
    @Transactional(readOnly = true)
    public long syncOne(long sharedOfficeId) {
        SharedOffice so = sharedOfficeRepository.findById(sharedOfficeId)
                .orElseThrow(() -> new IllegalArgumentException("SharedOffice not found: " + sharedOfficeId));

        AiSharedOfficeSnapshot snap = AiSharedOfficeSnapshotMapper.from(so);
        aiClient.upsertSharedOffices(List.of(snap));
        return so.getId();
    }
}