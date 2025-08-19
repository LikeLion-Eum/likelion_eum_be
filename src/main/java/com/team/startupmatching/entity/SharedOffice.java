package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
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

    // 호스트 정보
    @Column(name = "host_business_name", nullable = false, length = 100)
    private String hostBusinessName;            // 공간상호

    @Column(name = "host_representative_name", nullable = false, length = 50)
    private String hostRepresentativeName;      // 대표자명

    @Column(name = "host_address", nullable = false, length = 255)
    private String hostAddress;                 // 소재지

    @Column(name = "business_registration_number", nullable = false, length = 12)
    private String businessRegistrationNumber;  // 사업자번호(예: 123-45-67890) — unique 아님

    @Column(name = "host_contact", nullable = false, length = 30)
    private String hostContact;                 // 연락처
}
