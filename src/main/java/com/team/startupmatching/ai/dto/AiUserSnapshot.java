package com.team.startupmatching.ai.dto;

import java.util.List;

public class AiUserSnapshot {
    private final long id;              // 사용자 내부 식별자 (long 선호)
    private final String name;          // ※ PII 가능 → 필요 시 displayName로 교체 검토
    private final String career;        // 예: "junior", "senior" 또는 자유 텍스트
    private final String introduction;  // 자기소개
    private final List<String> skills;  // ["React","Spring"] 형태
    private final String location;      // 예: "충남 아산시"
    private final String resumeUrl;     // 이 프로젝트에선 'resum_url' 쓰면 여기도 맞춰 변경

    public AiUserSnapshot(long id, String name, String career, String introduction,
                          List<String> skills, String location, String resumeUrl) {
        this.id = id;
        this.name = name;
        this.career = career;
        this.introduction = introduction;
        this.skills = skills;
        this.location = location;
        this.resumeUrl = resumeUrl;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getCareer() { return career; }
    public String getIntroduction() { return introduction; }
    public List<String> getSkills() { return skills; }
    public String getLocation() { return location; }
    public String getResumeUrl() { return resumeUrl; }
}