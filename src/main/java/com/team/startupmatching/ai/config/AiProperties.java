package com.team.startupmatching.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    // 공통
    private String baseUrl;                 // 예: https://xxxxx.ngrok-free.app
    private Integer timeoutMs;              // 예: 15000
    private String authType;                // "x-api-key" | "bearer" | null
    private String apiKey;                  // 헤더 값

    // 업서트 경로 (유저 + IncubationCenter)
    private String usersUpsertPath;             // 예: /profiles
    private String incubationCentersUpsertPath; // 예: /incubation-centers

    // 추천 경로 (유저 + IncubationCenter)
    private String usersRecommendPath;             // 예: /recommend/users
    private String incubationCentersRecommendPath; // 예: /recommend/incubation-centers
}
