package com.team.startupmatching.ai.dto;

import java.util.List;
import java.util.UUID;

public class AiUpsertRequest {
    private final String schema;           // 예: "user-profile@v1"
    private final String idempotencyKey;   // 재시도 대비 고유 키
    private final List<AiUserSnapshot> items;

    public AiUpsertRequest(String schema, String idempotencyKey, List<AiUserSnapshot> items) {
        this.schema = schema;
        this.idempotencyKey = idempotencyKey;
        this.items = items;
    }

    public String getSchema() { return schema; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public List<AiUserSnapshot> getItems() { return items; }

    // 편의 생성기: 기본 스키마 + UUID 자동 생성
    public static AiUpsertRequest of(List<AiUserSnapshot> items) {
        return new AiUpsertRequest("user-profile@v1", UUID.randomUUID().toString(), items);
    }
}