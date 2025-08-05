package com.team.startupmatching.Specification;

import com.team.startupmatching.dto.SpaceSearchRequest;
import com.team.startupmatching.entity.Space;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

public class SpaceSpecification {

    public static Specification<Space> search(SpaceSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
                predicates.add(cb.like(root.get("location"), "%" + request.getLocation().trim() + "%"));
            }
            if (request.getSizeMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("size"), request.getSizeMin()));
            }
            if (request.getSizeMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("size"), request.getSizeMax()));
            }
            if (request.getPriceMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getPriceMin()));
            }
            if (request.getPriceMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getPriceMax()));
            }
            if (request.getType() != null && !request.getType().isEmpty()) {
                predicates.add(root.get("type").in(request.getType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
