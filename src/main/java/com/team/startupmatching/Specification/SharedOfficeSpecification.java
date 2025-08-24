package com.team.startupmatching.Specification;

import com.team.startupmatching.entity.SharedOffice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

public final class SharedOfficeSpecification {

    private SharedOfficeSpecification() {}

    /** 키워드: name / location / description LIKE %kw% */
    public static Specification<SharedOffice> keywordLike(String kw) {
        if (!StringUtils.hasText(kw)) return null;
        final String like = "%" + kw.trim() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(root.get("name"), like),
                cb.like(root.get("location"), like),
                cb.like(root.get("description"), like)
        );
    }

    /** 최소 월요금 이상 (feeMonthly >= min) */
    public static Specification<SharedOffice> feeMonthlyGoe(Long min) {
        if (min == null) return null;
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("feeMonthly"), min);
    }

    /** 최대 월요금 이하 (feeMonthly <= max) */
    public static Specification<SharedOffice> feeMonthlyLoe(Long max) {
        if (max == null) return null;
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("feeMonthly"), max);
    }

    /** 편의: 한 번에 빌드 */
    public static Specification<SharedOffice> build(String keyword, Long minFee, Long maxFee) {
        Specification<SharedOffice> spec = (root, q, cb) -> cb.conjunction();

        Specification<SharedOffice> k = keywordLike(keyword);
        if (k != null) spec = spec.and(k);

        Specification<SharedOffice> ge = feeMonthlyGoe(minFee);
        if (ge != null) spec = spec.and(ge);

        Specification<SharedOffice> le = feeMonthlyLoe(maxFee);
        if (le != null) spec = spec.and(le);

        return spec;
    }

    /**
     * 주소 컬럼(location)에 대해 토큰 중 하나라도 포함되면 매칭 (OR LIKE)
     * 예: tokens = ["전남","전라남도","나주시","나주"]
     */
    public static Specification<SharedOffice> locationContainsAny(List<String> tokens) {
        return (root, q, cb) -> {
            if (tokens == null || tokens.isEmpty()) {
                // 아무 것도 매칭시키지 않음 (추천용에서는 빈 결과 의도)
                return cb.disjunction();
            }
            List<Predicate> ors = tokens.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .map(t -> cb.like(root.get("location"), "%" + t + "%"))
                    .toList();
            if (ors.isEmpty()) return cb.disjunction();
            return cb.or(ors.toArray(new Predicate[0]));
        };
    }
}
