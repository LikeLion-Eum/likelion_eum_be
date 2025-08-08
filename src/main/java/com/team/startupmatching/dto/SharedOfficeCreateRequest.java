package com.team.startupmatching.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 생성자
public class SharedOfficeCreateRequest {

    private String name;        // 공간 이름
    private String description; // 공간 소개
    private Long roomCount;  // 공간 개수
    private Long size;       // 전체 크기
    private String location;    // 위치
    private Long maxCount;   // 최대 수용 인원
}