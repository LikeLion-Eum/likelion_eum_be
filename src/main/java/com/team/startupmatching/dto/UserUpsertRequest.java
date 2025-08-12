package com.team.startupmatching.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpsertRequest {
    private Long id;              // 없으면 생성, 있으면 수정
    private String name;
    private String career;
    private String introduction;
    private String skills;        // "React, Spring" 처럼 문자열로 받자 (뒤에서 파싱)
    private String location;
    private String resumeUrl;
}