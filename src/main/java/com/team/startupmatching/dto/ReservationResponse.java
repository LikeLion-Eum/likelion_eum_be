package com.team.startupmatching.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationResponse {

    // 식별자
    private Long id;

    // 어떤 오피스 예약인지
    private Long sharedOfficeId;

    // 예약자 기본 정보
    private String reserverName;
    private String reserverPhone;  // 응답에서는 하이픈 포함 등 보기 좋게 포맷해도 됨
    private String reserverEmail;

    // 이용 시작 일시 & 기간(개월)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    private Long months;

    // 선택 입력
    private String inquiryNote;

    // 생성 시각(감사/정렬용)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
