package com.example.transport.repository.specification;

import com.example.transport.model.Staff;
import org.springframework.data.jpa.domain.Specification;

public class StaffSearchSpecs {

    public static Specification<Staff> hasUserType(String userType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userType"), userType);
    }

    public static Specification<Staff> hasRoleType(String roleType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("roleType"), roleType);
    }

    public static Specification<Staff> hasNin(String nin) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nin"), nin);
    }

    public static Specification<Staff> hasBankAccountNo(String bankAccountNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bankAccountNo"), bankAccountNo);
    }
}
