package com.team.startupmatching.dto;

import com.team.startupmatching.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String location;
    private String introduction;
    private String skills;
    private String career;
    private String resumeUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse from(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .location(u.getLocation())
                .introduction(u.getIntroduction())
                .skills(u.getSkills())
                .career(u.getCareer())
                .resumeUrl(u.getResumeUrl())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
