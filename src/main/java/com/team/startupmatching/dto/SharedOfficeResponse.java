package com.team.startupmatching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 생성자
public class SharedOfficeResponse {

    private Long id;                       // PK
    private String name;                   // 공간 이름
    private String location;               // 위치
    private Long size;                     // 전체 크기
    private Long maxCount;                 // 최대 수용 인원
    private Long feeMonthly;               // ✅ 월 요금
    private String description;            // 설명

    // ✅ 호스트 정보
    private String hostRepresentativeName;     // 대표자명
    private String businessRegistrationNumber; // 사업자등록번호
    private String hostContact;                // 연락처
}
