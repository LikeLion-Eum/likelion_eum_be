package com.team.startupmatching.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.team.startupmatching.entity.Recruitment;

import java.util.List;
import java.util.Optional;

/**
 * 모집글에 대한 데이터베이스 접근을 담당하는 Repository
 */
@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> , JpaSpecificationExecutor<Recruitment> {
    // 기본적인 CRUD (save, findById, findAll, delete 등)는 JpaRepository가 자동 제공

    @EntityGraph(attributePaths = "user")
    List<Recruitment> findAll(Sort sort);

    // JpaSpecificationExecutor 의 시그니처를 "재선언"해서 @EntityGraph 적용
    @EntityGraph(attributePaths = "user")
    List<Recruitment> findAll(org.springframework.data.jpa.domain.Specification<Recruitment> spec, Sort sort);

    @EntityGraph(attributePaths = "user")
    Optional<Recruitment> findById(Long id);
}