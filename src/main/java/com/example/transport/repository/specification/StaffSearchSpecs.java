package com.example.transport.repository.specification;

import com.example.transport.enums.RoleType;
import com.example.transport.model.Staff;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StaffSearchSpecs {

    public static Specification<Staff> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            if (keyword.length() < 2) return null;

            String pattern = keyword.toLowerCase() + "%";

            List<Predicate> predicates = new ArrayList<>();

            try {
                RoleType roleType = RoleType.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("roleType"), roleType));
            } catch (IllegalArgumentException ignored) {}

            try {
                String email = keyword.toLowerCase();
                predicates.add(cb.equal(root.get("user").get("email"), email));
            } catch (IllegalArgumentException ignored) {}

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Staff> hasRoleType(String roleType) {
        return (root, query, cb) -> {
            try {
                RoleType role = RoleType.valueOf(roleType.toUpperCase());
                return cb.equal(root.get("roleType"), role);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Staff> hasNin(String nin) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nin"), nin);
    }

    public static Specification<Staff> hasBankName(String bankName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bankName"), bankName);
    }

    public static Specification<Staff> hasBankAccountNo(String bankAccountNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bankAccountNo"), bankAccountNo);
    }

    public static Specification<Staff> hasSalary(BigDecimal salary) {
        return (root, query, cb) ->
                cb.equal(root.get("salary"), salary);
    }

}
