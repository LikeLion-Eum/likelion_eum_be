package com.team.startupmatching.repository;

import com.team.startupmatching.entity.SharedOffice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SharedOfficeRepository
        extends JpaRepository<SharedOffice, Long>, JpaSpecificationExecutor<SharedOffice> {

    // 위치 검색(부분 일치, 대소문자 무시)
    List<SharedOffice> findByLocationContainingIgnoreCase(String location);

    // 페이징 버전(선택)
    Page<SharedOffice> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    // 호스트 정보 기반 보조 검색(선택)
    List<SharedOffice> findByHostBusinessNameContainingIgnoreCase(String hostBusinessName);
    List<SharedOffice> findByHostRepresentativeNameContainingIgnoreCase(String hostRepresentativeName);

    // 중복 방지 체크(선택): 같은 사업자번호 + 같은 공간명 등록 방지 용도
    boolean existsByBusinessRegistrationNumberAndName(String businessRegistrationNumber, String name);

    // 관리용: 특정 사업자번호로 가장 최근 등록된 오피스
    Optional<SharedOffice> findTopByBusinessRegistrationNumberOrderByIdDesc(String businessRegistrationNumber);
}
