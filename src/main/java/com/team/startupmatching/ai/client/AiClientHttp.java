package com.team.startupmatching.ai.client;

import com.team.startupmatching.ai.config.AiProperties;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
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

    @Override
    public void upsertOne(AiUserSnapshot snapshot) {
        var spec = aiWebClient.post().uri(props.getUpsertPath());

        // ⬇️ yml에 auth-type이 있을 때만 헤더 부착
        String t = props.getAuthType();
        if ("x-api-key".equalsIgnoreCase(t)) {
            spec.header("X-API-Key", props.getApiKey());
        } else if ("bearer".equalsIgnoreCase(t)) {
            spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey());
        }

        spec.bodyValue(snapshot)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofMillis(props.getTimeoutMs() != null ? props.getTimeoutMs() : 3000));
    }

    @Override
    public void upsertMany(List<AiUserSnapshot> snapshots) {
        var spec = aiWebClient.post().uri(props.getUpsertPath());

        String t = props.getAuthType();
        if ("x-api-key".equalsIgnoreCase(t)) {
            spec.header("X-API-Key", props.getApiKey());
        } else if ("bearer".equalsIgnoreCase(t)) {
            spec.header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey());
        }

        spec.bodyValue(snapshots)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofMillis(props.getTimeoutMs() != null ? props.getTimeoutMs() : 3000));
    }
}
