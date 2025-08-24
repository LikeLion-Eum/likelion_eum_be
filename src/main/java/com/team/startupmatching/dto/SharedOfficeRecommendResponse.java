package com.team.startupmatching.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.startupmatching.entity.SharedOffice;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedOfficeRecommendResponse {

    private Long id;
    private String name;
    private String description;
    private Long roomCount;
    private String location;
    private Long size;
    private Long maxCount;

    // ✅ 추천 카드에서 사용할 대표 이미지 URL
    private String mainPhotoUrl;

    // 호환용
    public static SharedOfficeRecommendResponse from(SharedOffice so) {
        return from(so, null);
    }

    // 대표 이미지까지 포함하는 팩토리
    public static SharedOfficeRecommendResponse from(SharedOffice so, String mainPhotoUrl) {
        return SharedOfficeRecommendResponse.builder()
                .id(so.getId())
                .name(so.getName())
                .description(so.getDescription())
                .roomCount(so.getRoomCount())
                .location(so.getLocation())
                .size(so.getSize())
                .maxCount(so.getMaxCount())
                .mainPhotoUrl(mainPhotoUrl)
                .build();
    }
}
