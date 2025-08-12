package com.team.startupmatching.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserCreateRequest {

    @NotBlank(message = "name 은 필수입니다.")
    private String name;

    @NotBlank(message = "email 은 필수입니다.")
    @Email(message = "email 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "location 은 필수입니다.")
    private String location;

    // 선택값
    private String introduction;
    private String skills;      // e.g. "React, Spring"
    private String career;      // e.g. "junior", "3년"
    private String resumeUrl;   // e.g. "https://.../resume.pdf"
}
