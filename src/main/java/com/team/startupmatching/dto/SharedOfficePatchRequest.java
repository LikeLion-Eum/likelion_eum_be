// package: com.team.startupmatching.dto
package com.team.startupmatching.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SharedOfficePatchRequest {
    private String name;
    private String location;
    private Long roomCount;
    private Long size;
    private Long maxCount;
    private Long feeMonthly;
    private String description;
    private String hostRepresentativeName;
    private String businessRegistrationNumber;
    private String hostContact;
}
