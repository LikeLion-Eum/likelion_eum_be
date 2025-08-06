package com.team.startupmatching.Specification;

import com.team.startupmatching.entity.Recruitment;
import org.springframework.data.jpa.domain.Specification;

public class RecruitmentSpecification {

    /**
     * keyword가 다음 필드 중 하나라도 포함되면 true:
     * - title
     * - content
     * - writer
     * - contact
     * - spaceName
     * - spaceLocation
     */
    public static Specification<Recruitment> containsKeywordEverywhere(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword + "%";

            return cb.or(
                    cb.like(root.get("title"), pattern),
                    cb.like(root.get("content"), pattern),
                    cb.like(root.get("writer"), pattern),
                    cb.like(root.get("contact"), pattern),
                    cb.like(root.get("spaceName"), pattern),
                    cb.like(root.get("spaceLocation"), pattern)
            );
        };
    }
}