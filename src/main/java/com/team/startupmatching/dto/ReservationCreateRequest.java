package com.team.startupmatching.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationCreateRequest {

    // 어떤 오피스에 대한 예약인지 (URL path로 받지 않고 바디로 받을 경우 사용)
    // 바디로 안 받는다면 이 필드는 제거하세요.
    private Long sharedOfficeId;

    // 예약자 기본 정보
    @NotBlank(message = "이름은 필수입니다.")
    private String reserverName;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(
            regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|0\\d{1,2}-?\\d{3,4}-?\\d{4})$",
            message = "전화번호 형식이 올바르지 않습니다. 하이픈은 선택입니다."
    )
    private String reserverPhone; // 서비스에서 숫자만 저장으로 정규화 권장

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String reserverEmail;

    // 이용 시작 일시 (ISO-8601, 예: 2025-09-01T09:00:00)
    @NotNull(message = "시작일시는 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    // 몇 개월 이용할지 (>= 1)
    @NotNull(message = "이용 개월 수는 필수입니다.")
    @Min(value = 1, message = "이용 개월 수는 1 이상이어야 합니다.")
    private Long months;

    // 선택 입력
    @Size(max = 2000, message = "문의 사항은 2000자 이하여야 합니다.")
    private String inquiryNote;
}
