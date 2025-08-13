package com.team.startupmatching.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class AiHttpConfig {

    private final AiProperties props;

    @Bean
    public WebClient aiWebClient() {
        // 최종 호출 URL = baseUrl + upsertPath
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }
}