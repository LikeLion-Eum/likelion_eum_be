package com.team.startupmatching.ai.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private boolean enabled = true;
    private String baseUrl;
    private String upsertPath;
    private long timeoutMs = 3000;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getUpsertPath() { return upsertPath; }
    public void setUpsertPath(String upsertPath) { this.upsertPath = upsertPath; }

    public long getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(long timeoutMs) { this.timeoutMs = timeoutMs; }
}