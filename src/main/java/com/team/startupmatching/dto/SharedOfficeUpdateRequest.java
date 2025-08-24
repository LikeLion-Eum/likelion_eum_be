package com.team.startupmatching.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SharedOfficeUpdateRequest {

    // 선택 업데이트가 가능하도록 전부 nullable(래퍼 타입)로
    private String  name;
    private String  description;
    private Long roomCount;
    private Long size;
    private String  location;
    private Long maxCount;
    private Long    feeMonthly;

    // 호스트(사업자/연락처)도 선택 수정
    private String  hostRepresentativeName;
    private String  businessRegistrationNumber; // 하이픈 포함/미포함 허용
    private String  hostContact;                 // 하이픈 포함/미포함 허용
}
