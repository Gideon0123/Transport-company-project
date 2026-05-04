package com.example.transport.repository.specification;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import com.example.transport.enums.UserType;
import com.example.transport.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSearchSpecs {

    public static Specification<User> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            if (keyword.length() < 2) return null;

            List<Predicate> predicates = new ArrayList<>();

            String pattern = keyword.toLowerCase() + "%";

            predicates.add(cb.like(cb.lower(root.get("firstName")), pattern));
            predicates.add(cb.like(cb.lower(root.get("lastName")), pattern));
            predicates.add(cb.like(cb.lower(root.get("email")), pattern));
            predicates.add(cb.like(cb.lower(root.get("phoneNo")), pattern));

            try {
                UserType userType = UserType.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("userType"), userType));
            } catch (IllegalArgumentException ignored) {}

            try {
                RoleType roleType = RoleType.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("roleType"), roleType));
            } catch (IllegalArgumentException ignored) {}

            try {
                UserStatus status = UserStatus.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException ignored) {}

            try {

                predicates.add(cb.like(cb.lower(root.get("firstName")), pattern));
                predicates.add(cb.like(cb.lower(root.get("lastName")), pattern));

                predicates.add(
                        cb.like(
                                cb.lower(
                                        cb.concat(
                                                cb.concat(root.get("firstName"), " "),
                                                root.get("lastName")
                                        )
                                ),
                                pattern
                        )
                );

                predicates.add(
                        cb.like(
                                cb.lower(
                                        cb.concat(
                                                cb.concat(root.get("lastName"), " "),
                                                root.get("firstName")
                                        )
                                ),
                                pattern
                        )
                );

            } catch (Exception ignored) {}

            return cb.or(predicates.toArray(new Predicate[0]));
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

    public static Specification<User> hasRoleType(String roleType) {
        return (root, query, cb) -> {
            try {
                RoleType role = RoleType.valueOf(roleType.toUpperCase());
                return cb.equal(root.get("roleType"), role);
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
