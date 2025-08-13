package com.team.startupmatching.ai.config;



import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private Boolean enabled;
    private String baseUrl;     // 예: http://localhost:9000
    private String upsertPath;  // 예: /v1/users/upsert
    private Integer timeoutMs;  // 예: 3000
    private String authType;    // "x-api-key" | "bearer" | null
    private String apiKey;      // 헤더에 넣을 값
}