package com.team.startupmatching.ai.mapper;

import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.entity.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class AiUserSnapshotMapper {

    public AiUserSnapshot toSnapshot(User user) {
        return new AiUserSnapshot(
                user.getId(),
                user.getName(),
                user.getCareer(),
                user.getIntroduction(),
                normalizeSkills(user.getSkills()), // 엔티티 skills가 String(CSV)라고 가정
                user.getLocation(),
                user.getResumeUrl()
        );
    }

    private String normalizeSkills(String raw) {
        if (raw == null || raw.isBlank()) return "";
        return Arrays.stream(raw.split("[,;/|，]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
    }
}