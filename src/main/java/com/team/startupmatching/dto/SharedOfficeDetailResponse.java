package com.team.startupmatching.dto;

import com.team.startupmatching.dto.photo.PhotoItemResponse;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SharedOfficeDetailResponse {
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
}
