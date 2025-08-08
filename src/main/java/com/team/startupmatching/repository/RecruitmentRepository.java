package com.team.startupmatching.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.team.startupmatching.entity.Recruitment;

/**
 * 모집글에 대한 데이터베이스 접근을 담당하는 Repository
 */
@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> , JpaSpecificationExecutor<Recruitment> {
    // 기본적인 CRUD (save, findById, findAll, delete 등)는 JpaRepository가 자동 제공
}