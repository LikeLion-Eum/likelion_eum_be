package com.team.startupmatching.repository;

import com.team.startupmatching.entity.IncubationCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncubationCenterRepository
        extends JpaRepository<IncubationCenter, Long>, JpaSpecificationExecutor<IncubationCenter> {

    // 업서트용(중복 방지) - sourceId로 존재 여부 체크
    Optional<IncubationCenter> findBySourceId(String sourceId);

    // 지역 검색(부분 일치, 대소문자 무시)
    List<IncubationCenter> findByRegionContainingIgnoreCase(String region);

    // 지역 + 모집중만
    List<IncubationCenter> findByRegionContainingIgnoreCaseAndRecruitingTrue(String region);

    // 접수기간이 특정 구간과 겹치는 공고(시작 ≤ end && 종료 ≥ start)
    List<IncubationCenter> findByReceiptStartDateLessThanEqualAndReceiptEndDateGreaterThanEqual(
            LocalDate endInclusive,
            LocalDate startInclusive
    );
}
