package com.team.startupmatching.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class SpaceSearchRequest {
    private String location;
    private Long sizeMin;
    private Long sizeMax;
    private Long priceMin;
    private Long priceMax;
    private List<String> type; // 여러 타입 필터링 가능
}
