package com.team.startupmatching.ai.mapper;

import com.team.startupmatching.ai.dto.AiIncubationCenterSnapshot;
import com.team.startupmatching.entity.IncubationCenter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * IncubationCenter 엔티티 → AI 업서트용 스냅샷 매퍼
 */
@Component
public class AiIncubationCenterSnapshotMapper {

    public AiIncubationCenterSnapshot toSnapshot(IncubationCenter e) {
        Objects.requireNonNull(e, "IncubationCenter must not be null");

        return AiIncubationCenterSnapshot.builder()
                .id(e.getId())
                .title(e.getTitle())                       // NOT NULL
                .region(e.getRegion())                     // NOT NULL
                .supportField(e.getSupportField())         // nullable
                .receiptStartDate(e.getReceiptStartDate()) // DATE nullable
                .receiptEndDate(e.getReceiptEndDate())     // DATE nullable
                .recruiting(e.isRecruiting())             // BIT(1) NOT NULL (boolean/Boolean)
                .applyUrl(e.getApplyUrl())                 // nullable
                .build();
    }

    /** 배치 편의 함수 (단건 반복 전송 전에 스냅샷으로 변환) */
    public List<AiIncubationCenterSnapshot> toSnapshots(List<IncubationCenter> list) {
        if (list == null) return List.of();
        return list.stream().map(this::toSnapshot).collect(Collectors.toList());
    }
}
