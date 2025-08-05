package com.team.startupmatching.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SpaceResponse {

    private final Long id;
    private final String name;
    private final String location;
    private final Long size;
    private final Long price;
    private final String type;

    @Builder
    public SpaceResponse(Long id, String name, String location, Long size, Long price, String type) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.size = size;
        this.price = price;
        this.type = type;
    }
}
