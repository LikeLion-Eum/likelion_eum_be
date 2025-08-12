package com.team.startupmatching.ai.client;


import com.team.startupmatching.ai.config.AiProperties;
import com.team.startupmatching.ai.dto.AiUpsertRequest;
import com.team.startupmatching.ai.dto.AiUserSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiClientHttp implements AiClient {

    private final WebClient.Builder webClientBuilder;
    private final AiProperties props;

    @Override
    public void upsertUsers(List<AiUserSnapshot> items) {
        if (!props.isEnabled() || items == null || items.isEmpty()) return;

        var req = AiUpsertRequest.of(items);

        try {
            var client = webClientBuilder
                    .baseUrl(Objects.requireNonNull(props.getBaseUrl(), "ai.base-url is null"))
                    .build();

            client.post()
                    .uri(props.getUpsertPath())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(req)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofMillis(props.getTimeoutMs()));

            log.debug("[AI] upsertUsers ok: items={}", items.size());
        } catch (Exception e) {
            log.warn("[AI] upsertUsers failed: {}", e.toString());
            // MVP에선 삼키고 로그만. (나중엔 재시도 큐로 보내자)
        }
    }
}
