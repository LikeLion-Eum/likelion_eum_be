package com.team.startupmatching.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDate;

/**
 * 지원사업(IncubationCenter) 업서트 전송용 스냅샷
 * 전송 필드: id, title, region, support_field, receipt_start_date, receipt_end_date, recruiting, apply_url
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiIncubationCenterSnapshot {
    private Long id;
    private String title;
    private String region;
    private String supportField;
    private LocalDate receiptStartDate;
    private LocalDate receiptEndDate;
    private Boolean recruiting;
    private String applyUrl;
}
