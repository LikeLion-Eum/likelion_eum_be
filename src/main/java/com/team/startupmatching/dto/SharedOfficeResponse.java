package com.team.startupmatching.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 응답에서 생략 (선택)
public class SharedOfficeResponse {

    private Long id;                        // PK
    private String name;                    // 공간 이름
    private String location;                // 위치
    private Long size;                      // 전체 크기
    private Long maxCount;                  // 최대 수용 인원
    private Long feeMonthly;                // 월 요금
    private String description;             // 설명

    // 호스트 정보
    private String hostRepresentativeName;      // 대표자명
    private String businessRegistrationNumber;  // 사업자등록번호
    private String hostContact;                 // 연락처

    // ✅ 목록/추천 카드에 쓸 대표 이미지 URL
    private String mainPhotoUrl;
}
