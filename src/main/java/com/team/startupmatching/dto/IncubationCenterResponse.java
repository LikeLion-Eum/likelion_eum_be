// IncubationCenterResponse.java
package com.team.startupmatching.dto;

import com.team.startupmatching.entity.IncubationCenter;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IncubationCenterResponse {
    private Long id;
    private String sourceId;
    private String title;
    private String region;
    private String supportField;
    private LocalDate receiptStartDate;
    private LocalDate receiptEndDate;
    private boolean recruiting;
    private String applyUrl;

    public static IncubationCenterResponse from(IncubationCenter e) {
        return IncubationCenterResponse.builder()
                .id(e.getId())
                .sourceId(e.getSourceId())
                .title(e.getTitle())
                .region(e.getRegion())
                .supportField(e.getSupportField())
                .receiptStartDate(e.getReceiptStartDate())
                .receiptEndDate(e.getReceiptEndDate())
                .recruiting(e.isRecruiting())
                .applyUrl(e.getApplyUrl())
                .build();
    }
}
