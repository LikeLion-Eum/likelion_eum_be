package com.team.startupmatching.repository;

import com.team.startupmatching.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 모집글에 대한 데이터베이스 접근을 담당하는 Repository
 */
@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    // 기본적인 CRUD (save, findById, findAll, delete 등)는 JpaRepository가 자동 제공
}