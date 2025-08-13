package com.team.startupmatching.ai.client;

import com.team.startupmatching.ai.dto.AiIncubationCenterSnapshot;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.ai.dto.IncubationCenterRecommendationItem;
import com.team.startupmatching.ai.dto.RecommendRequest;
import com.team.startupmatching.ai.dto.UserRecommendationItem;

import java.util.List;

/**
 * AI 연동 클라이언트 (업서트 + 추천)
 * - 업서트: User, IncubationCenter(지원사업)
 * - 추천: UserRecommendationItem / IncubationCenterRecommendationItem
 */
public interface AiClient {

    // ===== 업서트 =====
    void upsertUser(AiUserSnapshot snapshot);

    void upsertIncubationCenter(AiIncubationCenterSnapshot snapshot);

    // 배치 업서트(단건 반복 기본 구현)
    default void upsertUsers(List<AiUserSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) return;
        for (AiUserSnapshot s : snapshots) upsertUser(s);
    }

    default void upsertIncubationCenters(List<AiIncubationCenterSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) return;
        for (AiIncubationCenterSnapshot s : snapshots) upsertIncubationCenter(s);
    }

    // ===== 추천 =====
    List<UserRecommendationItem> recommendUsers(RecommendRequest req);

    List<IncubationCenterRecommendationItem> recommendIncubationCenters(RecommendRequest req);

    // ===== 하위호환 브리지 =====
    /** @deprecated 예전 단일 recommend → 유저 추천으로 매핑 */
    @Deprecated
    default List<UserRecommendationItem> recommend(RecommendRequest req) {
        return recommendUsers(req);
    }

    /** @deprecated 예전 유저 업서트 단건 메서드명 */
    @Deprecated
    default void upsertOne(AiUserSnapshot snapshot) {
        upsertUser(snapshot);
    }

    /** @deprecated 예전 유저 업서트 배치 메서드명 */
    @Deprecated
    default void upsertMany(List<AiUserSnapshot> snapshots) {
        upsertUsers(snapshots);
    }
}
