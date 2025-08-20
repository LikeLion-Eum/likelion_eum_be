package com.team.startupmatching.repository;

import com.team.startupmatching.entity.SharedOffice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SharedOfficeRepository
        extends JpaRepository<SharedOffice, Long>, JpaSpecificationExecutor<SharedOffice> {

    // 위치 검색(부분 일치, 대소문자 무시)
    List<SharedOffice> findByLocationContainingIgnoreCase(String location);

    // 위치 검색 + 페이징
    Page<SharedOffice> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    // 이름 검색(선택)
    List<SharedOffice> findByNameContainingIgnoreCase(String name);

    // 이름 검색 + 페이징(선택)
    Page<SharedOffice> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
