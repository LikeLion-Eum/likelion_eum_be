package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shared_office")
public class SharedOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기본 정보
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Long roomCount;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private Long maxCount;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ✅ 월 요금 (원 단위, 숫자)
    @Column(name = "fee_monthly")
    private Long feeMonthly;

    // 호스트 정보
    @Column(name = "host_representative_name", nullable = false, length = 50)
    private String hostRepresentativeName;      // 대표자명

    @Column(name = "business_registration_number", nullable = false, length = 12)
    private String businessRegistrationNumber;  // 사업자등록번호(숫자 10자리 or 하이픈 포함 최대 12)

    @Column(name = "host_contact", nullable = false, length = 30)
    private String hostContact;                 // 연락처(숫자만 저장 권장)
}
