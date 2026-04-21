package com.example.transport.repository.specification;

import com.example.transport.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSearchSpecs {

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), firstName);
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<User> hasPhoneNo(String phoneNo) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("phoneNo"), phoneNo);
    }
}
