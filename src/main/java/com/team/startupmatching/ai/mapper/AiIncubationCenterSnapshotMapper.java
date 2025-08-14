package com.team.startupmatching.ai.mapper;

import com.team.startupmatching.ai.dto.AiIncubationCenterSnapshot;
import com.team.startupmatching.entity.IncubationCenter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * IncubationCenter 엔티티 → AI 업서트용 스냅샷 매퍼
 */
@Component
public class AiIncubationCenterSnapshotMapper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public AiIncubationCenterSnapshot toSnapshot(IncubationCenter e) {
        Objects.requireNonNull(e, "IncubationCenter must not be null");

        return AiIncubationCenterSnapshot.builder()
                .id(e.getId())
                .title(e.getTitle())                       // NOT NULL
                .region(e.getRegion())
                .supportField(nzb(e.getSupportField()))// NOT NULL
                .receiptStartDate(fmt(e.getReceiptStartDate())) // ✅ LocalDate → String
                .receiptEndDate(fmt(e.getReceiptEndDate()))     // ✅ LocalDate → String
                .recruiting(e.isRecruiting())             // BIT(1) NOT NULL (boolean/Boolean)
                .applyUrl(e.getApplyUrl())                 // nullable
                .build();
    }

    private static String fmt(LocalDate d) {
        return (d == null) ? null : DATE_FMT.format(d);
    }

    private static String nzb(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }

    /** 배치 편의 함수 (단건 반복 전송 전에 스냅샷으로 변환) */
    public List<AiIncubationCenterSnapshot> toSnapshots(List<IncubationCenter> list) {
        if (list == null) return List.of();
        return list.stream().map(this::toSnapshot).collect(Collectors.toList());
    }
}
