package com.team.startupmatching.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // 선택: null 필드 제외
public class AiUserSnapshot {
    private final Long id;              // 내부 식별자
    private final String name;
    private final String career;        // 예: "junior", "senior"
    private final String introduction;  // 자기소개
    private final String skills;  // 예: ["React","Spring"]
    private final String location;      // 예: "충남 아산시"
    private final String resumeUrl;     // 이력서 URL

    public AiUserSnapshot(
            Long id,
            String name,
            String career,
            String introduction,
            String skills,
            String location,
            String resumeUrl
    ) {
        this.id = id;
        this.name = name;
        this.career = career;
        this.introduction = introduction;
        this.skills = skills;
        this.location = location;
        this.resumeUrl = resumeUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCareer() { return career; }
    public String getIntroduction() { return introduction; }
    public String getSkills() { return skills; }
    public String getLocation() { return location; }
    public String getResumeUrl() { return resumeUrl; }
}
