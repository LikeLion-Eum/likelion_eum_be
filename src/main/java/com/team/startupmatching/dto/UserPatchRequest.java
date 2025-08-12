package com.team.startupmatching.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null로 온 필드는 "변경 안 함"
public class UserPatchRequest {
    private String name;
    private String location;
    private String introduction;
    private String skills;
    private String career;
    private String resumeUrl;
}
