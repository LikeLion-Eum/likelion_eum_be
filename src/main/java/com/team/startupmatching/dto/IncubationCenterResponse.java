package com.team.startupmatching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 생성자
public class IncubationCenterResponse {

    private Long id;       // PK
    private String description;
    private String region; // 위치 지역
    private String siteUrl;// 사이트 주소
}