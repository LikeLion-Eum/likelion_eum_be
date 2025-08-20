package com.team.startupmatching.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SharedOfficeCreateRequest {

    // 기본 정보
    @NotBlank private String name;
    @NotBlank private String location;
    @NotNull private Long roomCount;
    @NotNull private Long size;
    @NotNull private Long maxCount;
    private String description;

    // 호스트 정보
    @NotBlank private String hostRepresentativeName;    // 대표자명

    // ✅ 하이픈 선택 입력 허용 (예: 1234567890 또는 123-45-67890)
    @NotBlank
    @Pattern(
            regexp = "^\\d{3}-?\\d{2}-?\\d{5}$",
            message = "사업자번호는 10자리 숫자이며 하이픈은 선택입니다. 예) 1234567890 또는 123-45-67890"
    )
    private String businessRegistrationNumber;

    // ✅ 휴대폰/유선 공통, 하이픈 선택 (예: 01012345678, 010-1234-5678, 02-123-4567 등)
    @NotBlank
    @Pattern(
            regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|0\\d{1,2}-?\\d{3,4}-?\\d{4})$",
            message = "연락처 형식이 올바르지 않습니다. 하이픈은 선택입니다."
    )
    private String hostContact;
}
