package com.team.startupmatching.dto;

import com.team.startupmatching.entity.SharedOffice;
import com.team.startupmatching.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 생성자
@Builder
public class SharedOfficeResponse {

    private Long id;           // PK
    private String name;       // 공간 이름
    private String location;   // 위치
    private Long size;      // 전체 크기
    private Long maxCount;  // 최대 수용 인원

    public static SharedOfficeResponse from(SharedOffice so) {
        return SharedOfficeResponse.builder()
                .id(so.getId())
                .name(so.getName())
                .location(so.getLocation())
                .maxCount(so.getMaxCount())
                .size(so.getSize())
                .build();
    }
}