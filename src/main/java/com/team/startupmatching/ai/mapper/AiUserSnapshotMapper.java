package com.team.startupmatching.ai.mapper;

import com.team.startupmatching.ai.dto.AiUserSnapshot;
import com.team.startupmatching.entity.User;

import java.util.Arrays;
import java.util.List;

public class AiUserSnapshotMapper {

    /** User 엔티티 → AI로 보낼 스냅샷 */
    public static AiUserSnapshot from(User u) {
        return new AiUserSnapshot(
                u.getId(),
                nullSafe(u.getName()),
                nullSafe(u.getCareer()),
                nullSafe(u.getIntroduction()),
                toList(u.getSkills()),                  // "React, JS" → ["React","JS"]
                nullSafe(u.getLocation()),
                nullSafe(u.getResumeUrl())
        );
    }

    private static List<String> toList(String skills) {
        if (skills == null || skills.isBlank()) return List.of();
        return Arrays.stream(skills.split("[,/]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String nullSafe(String s) {
        return (s == null) ? "" : s;
    }
}