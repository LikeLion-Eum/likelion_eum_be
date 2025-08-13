package com.team.startupmatching.ai.client;

import com.team.startupmatching.ai.config.AiProperties;
import com.team.startupmatching.ai.dto.AiIncubationCenterSnapshot;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.ai.dto.IncubationCenterRecommendationItem;
import com.team.startupmatching.ai.dto.RecommendRequest;
import com.team.startupmatching.ai.dto.UserRecommendationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AiClientHttp implements AiClient {

    private final WebClient aiWebClient;
    private final AiProperties props;

    private WebClient.RequestBodySpec postWithAuth(String path) {
        WebClient.RequestBodySpec spec = aiWebClient.post().uri(path);
        String t = props.getAuthType();
        if ("x-api-key".equalsIgnoreCase(t)) {
            spec = spec.header("X-API-Key", props.getApiKey());
        } else if ("bearer".equalsIgnoreCase(t)) {
            spec = spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey());
        }
        return spec;
    }

    // ===== 업서트: 유저 =====
    @Override
    public void upsertUser(AiUserSnapshot snapshot) {
        int ms = props.getTimeoutMs() != null ? props.getTimeoutMs() : 3000;
        postWithAuth(props.getUsersUpsertPath())
                .bodyValue(snapshot)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofMillis(ms));
    }

    // ===== 업서트: 지원사업(IncubationCenter) =====
    @Override
    public void upsertIncubationCenter(AiIncubationCenterSnapshot snapshot) {
        int ms = props.getTimeoutMs() != null ? props.getTimeoutMs() : 3000;
        postWithAuth(props.getIncubationCentersUpsertPath())
                .bodyValue(snapshot)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofMillis(ms));
    }

    // ===== 추천: 유저 =====
    @Override
    public List<UserRecommendationItem> recommendUsers(RecommendRequest req) {
        int ms = props.getTimeoutMs() != null ? props.getTimeoutMs() : 10000;
        return postWithAuth(props.getUsersRecommendPath())
                .bodyValue(req)
                .retrieve()
                .bodyToFlux(UserRecommendationItem.class) // 배열 JSON
                .collectList()
                .block(Duration.ofMillis(ms));
    }

    // ===== 추천: 지원사업(IncubationCenter) =====
    @Override
    public List<IncubationCenterRecommendationItem> recommendIncubationCenters(RecommendRequest req) {
        int ms = props.getTimeoutMs() != null ? props.getTimeoutMs() : 10000;
        return postWithAuth(props.getIncubationCentersRecommendPath())
                .bodyValue(req)
                .retrieve()
                .bodyToFlux(IncubationCenterRecommendationItem.class) // ★ 전용 DTO로 파싱
                .collectList()
                .block(Duration.ofMillis(ms));
    }
}

