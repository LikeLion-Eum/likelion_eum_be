package com.team.startupmatching.Specification;

import com.team.startupmatching.entity.Recruitment;
import com.team.startupmatching.entity.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class RecruitmentSpecification {

    /**
     * 제목/내용/지역/직무/기술/경력/작성자이름 에서 키워드 포함 검색
     */
    public static Specification<Recruitment> containsKeywordEverywhere(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        final String like = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> {
            // 작성자 이름 검색 포함 (LEFT JOIN)
            Join<Recruitment, User> userJoin = root.join("user", JoinType.LEFT);
            query.distinct(true);

            Predicate p = cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("content")), like),
                    cb.like(cb.lower(root.get("location")), like),
                    cb.like(cb.lower(root.get("position")), like),
                    cb.like(cb.lower(root.get("skills")), like),
                    cb.like(cb.lower(root.get("career")), like),
                    cb.like(cb.lower(userJoin.get("name")), like)
            );

            return p;
        };
    }

    /**
     * 공백으로 나눈 여러 키워드를 모두 포함(and) 검색
     * 예) "신창 프론트" → 신창 포함 & 프론트 포함
     */
    public static Specification<Recruitment> containsAllKeywords(String keywords) {
        if (keywords == null || keywords.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        String[] parts = keywords.trim().split("\\s+");

        // 빈 스펙(항상 참)에서 시작
        Specification<Recruitment> spec = (root, query, cb) -> cb.conjunction();

        for (String part : parts) {
            spec = spec.and(containsKeywordEverywhere(part));
        }
        return spec;
    }
}
