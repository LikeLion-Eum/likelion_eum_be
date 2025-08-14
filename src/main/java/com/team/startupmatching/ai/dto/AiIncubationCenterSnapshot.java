package com.team.startupmatching.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiIncubationCenterSnapshot {
    // 상대가 id를 문자열로 요구 → 직렬화 시 문자열로 강제
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String title;
    private String region;

    // 상대 FastAPI가 alias=camelCase 를 요구하는 필드들
    @JsonProperty("supportField")
    private String supportField;

    // 날짜도 문자열로 받음(YYYY-MM-DD)
    @JsonProperty("receiptStartDate")
    private String receiptStartDate;

    @JsonProperty("receiptEndDate")
    private String receiptEndDate;

    private Boolean recruiting;

    @JsonProperty("applyUrl")
    private String applyUrl;
}
