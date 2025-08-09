package com.team.startupmatching.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
@AllArgsConstructor
@Builder
@Entity
public class IncubationCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원본(API) 식별자 (예: pbanc_sn 1순위, 없으면 id fallback)
    @Column(nullable = false, unique = true, length = 100)
    private String sourceId;

    // 지원사업 공고명 (biz_pbanc_nm)
    @Column(nullable = false, length = 300)
    private String title;

    // 지역 (supt_regin)
    @Column(nullable = false, length = 100)
    private String region;

    // 지원분야 (supt_biz_clsfc)
    @Column(length = 200)
    private String supportField;

    // 공고 접수 시작/종료 (pbanc_rcpt_bgng_dt / pbanc_rcpt_end_dt, yyyyMMdd)
    private LocalDate receiptStartDate;
    private LocalDate receiptEndDate;

    // 모집 진행 여부 (rcrt_prgs_yn: Y/N → boolean)
    @Column(nullable = false)
    private boolean recruiting;

    // 사이트 URL (biz_aply_url → 없으면 온라인접수/상세/안내 순으로 채우기)
    @Column(length = 500)
    private String applyUrl;
}