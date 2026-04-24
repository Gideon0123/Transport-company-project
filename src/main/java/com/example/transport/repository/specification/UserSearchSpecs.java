package com.example.transport.repository.specification;

import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import com.example.transport.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSearchSpecs {

    public static Specification<User> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            if (keyword.length() < 2) return null;

            String pattern = keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("status")), pattern)
            );
        };
    }

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

    public static Specification<User> hasUserType(String userType) {
        return (root, query, cb) -> {
            try {
                UserType type = UserType.valueOf(userType.toUpperCase());
                return cb.equal(root.get("userType"), type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<User> hasUserStatus(String userStatus) {
        return (root, query, cb) -> {
            try {
                UserStatus status = UserStatus.valueOf(userStatus.toUpperCase());
                return cb.equal(root.get("status"), status);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

}
