package com.team.startupmatching.ai.dto;

import java.util.List;
import java.util.UUID;

public class AiSharedOfficeUpsertRequest {
    private final String schema;
    private final String idempotencyKey;
    private final List<AiSharedOfficeSnapshot> items; // 타입 변경

    public AiSharedOfficeUpsertRequest(String schema, String idempotencyKey, List<AiSharedOfficeSnapshot> items) { // 타입 변경
        this.schema = schema;
        this.idempotencyKey = idempotencyKey;
        this.items = items;
    }

    public String getSchema() { return schema; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public List<AiSharedOfficeSnapshot> getItems() { return items; }

    // 편의 생성기: 기본 스키마 + UUID 자동 생성
    public static AiSharedOfficeUpsertRequest of(List<AiSharedOfficeSnapshot> items) { // 타입 변경
        return new AiSharedOfficeUpsertRequest("shared-office@v1", UUID.randomUUID().toString(), items); // 스키마 이름 변경
    }
}
