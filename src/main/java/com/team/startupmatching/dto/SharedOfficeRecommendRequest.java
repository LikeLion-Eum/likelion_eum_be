package com.team.startupmatching.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharedOfficeRecommendRequest {

    @NotBlank(message = "location은 필수 요청 값입니다.")
    private String location;
}