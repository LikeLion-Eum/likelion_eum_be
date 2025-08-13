package com.team.startupmatching.event;

/**
 * IncubationCenter 엔티티가 생성/수정되어 커밋된 뒤
 * AI 업서트를 트리거하기 위한 도메인 이벤트.
 */
public record IncubationCenterChangedEvent(Long incubationCenterId) {}
