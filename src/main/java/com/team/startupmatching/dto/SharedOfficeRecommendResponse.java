package com.team.startupmatching.dto;

import com.team.startupmatching.entity.SharedOffice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharedOfficeRecommendResponse {

    private Long id;
    private String name;
    private String description;
    private Long roomCount;
    private String location;
    private Long size;
    private Long maxCount;

    public static SharedOfficeRecommendResponse from(SharedOffice so) {
        return new SharedOfficeRecommendResponse(
                so.getId(),
                so.getName(),
                so.getDescription(),
                so.getRoomCount(),
                so.getLocation(),
                so.getSize(),
                so.getMaxCount()
        );
    }
}