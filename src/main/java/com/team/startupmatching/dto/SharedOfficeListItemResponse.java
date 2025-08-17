package com.team.startupmatching.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SharedOfficeListItemResponse {
    private Long id;
    private String name;
    private String location;
    private Long size;
    private Long maxCount;

    // 카드 썸네일/요약
    private String mainPhotoUrl;     // 대표 사진
    private Integer photoCount;      // 전체 사진 수
    private String shortDescription; // 리스트용 한 줄 소개

    // 선택: 편의시설 태그(있으면 노출)
    private List<String> facilities;
}
