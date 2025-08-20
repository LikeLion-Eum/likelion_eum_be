package com.team.startupmatching.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.startupmatching.dto.photo.PhotoItemResponse;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedOfficeDetailResponse {

    // 기본 정보
    private Long id;
    private String name;
    private String description;
    private Long roomCount;
    private Long size;
    private String location;
    private Long maxCount;

    // 편의시설(직방 스타일 태그)
    private List<String> facilities;   // ex) ["와이파이","프로젝터","주차",...]

    // 사진
    private String mainPhotoUrl;              // 대표 사진
    private List<PhotoItemResponse> photos;   // 전체 사진(seq ASC)

    // === 호스트 정보(신규) ===
    private String hostRepresentativeName;     // 대표자명
    private String businessRegistrationNumber; // 사업자번호(표시는 123-45-67890처럼)
    private String hostContact;                // 연락처(표시는 010-1234-5678처럼)
}
