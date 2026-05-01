package com.example.transport.repository.specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericSearchSpecification<T> {

    public Specification<T> build(Map<String, Object> filters) {

        return (root, query, cb) -> {

            if (filters == null || filters.isEmpty()) return null;

            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((field, value) -> {

                if (value == null) return;

                if (field.contains(".")) {
                    String[] parts = field.split("\\.");

                    Path<?> path = root.get(parts[0]);
                    for (int i = 1; i < parts.length; i++) {
                        path = path.get(parts[i]);
                    }

                    predicates.add(cb.equal(path, value));
                } else {
                    predicates.add(cb.equal(root.get(field), value));
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}