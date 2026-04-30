package com.example.transport.repository.specification;

import com.example.transport.dto.SearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GenericSearchSpecification<T> {

    public Specification<T> build(SearchRequest request) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 🔹 Filters
            if (request.getFilters() != null) {
                request.getFilters().forEach((key, value) -> {
                    predicates.add(cb.equal(root.get(key), value));
                });
            }

            // 🔹 Keyword
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String pattern = request.getKeyword().toLowerCase() + "%";
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}