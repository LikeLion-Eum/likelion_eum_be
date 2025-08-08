package com.team.startupmatching.repository;

import com.team.startupmatching.entity.IncubationCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IncubationCenterRepository
        extends JpaRepository<IncubationCenter, Long>, JpaSpecificationExecutor<IncubationCenter> {

    // 지역 검색(부분 일치, 대소문자 무시)
    List<IncubationCenter> findByRegionContainingIgnoreCase(String region);
}