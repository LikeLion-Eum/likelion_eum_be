package com.team.startupmatching.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${storage.local.base-dir:uploads}")
    private String baseDir; // 예: uploads  (실제 디스크: {프로젝트}/uploads)

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/**  →  file:{abs(baseDir)}/**
        String location = Paths.get(baseDir).toAbsolutePath().normalize().toUri().toString(); // file:/.../uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(0); // 개발 중 캐시 끔(운영 시 조정 가능)
    }
}
