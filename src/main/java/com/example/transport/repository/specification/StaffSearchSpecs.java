package com.example.transport.repository.specification;

import com.example.transport.enums.RoleType;
import com.example.transport.enums.UserStatus;
import com.example.transport.model.Staff;
import com.example.transport.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

            List<Predicate> predicates = new ArrayList<>();

            String pattern = keyword.toLowerCase() + "%";

            predicates.add(cb.like(cb.lower(root.get("nin")), pattern));
            predicates.add(cb.like(cb.lower(root.get("bankName")), pattern));
            predicates.add(cb.like(cb.lower(root.get("bankAccountNo")), pattern));

            try {
                BigDecimal salary = new BigDecimal(keyword);
                predicates.add(cb.equal(root.get("salary"), salary));
            } catch (Exception ignored) {}

            try {
                Long userId = Long.valueOf(keyword);
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            } catch (NumberFormatException ignored) {}

            try {
                RoleType roleType = RoleType.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("roleType"), roleType));
            } catch (IllegalArgumentException ignored) {}

            try {
                UserStatus status = UserStatus.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException ignored) {}

            try {
                String email = keyword.toLowerCase();
                predicates.add(cb.equal(root.get("user").get("email"), email));
            } catch (IllegalArgumentException ignored) {}

            try {
                String phoneNo = keyword.toLowerCase();
                predicates.add(cb.equal(root.get("user").get("phoneNo"), phoneNo));
            } catch (IllegalArgumentException ignored) {}

            try {
                Join<Staff, User> user = root.join("user", JoinType.LEFT);

                predicates.add(cb.like(cb.lower(user.get("firstName")), pattern));
                predicates.add(cb.like(cb.lower(user.get("lastName")), pattern));

                predicates.add(
                        cb.like(
                                cb.lower(
                                        cb.concat(
                                                cb.concat(user.get("firstName"), " "),
                                                user.get("lastName")
                                        )
                                ),
                                pattern
                        )
                );

                predicates.add(
                        cb.like(
                                cb.lower(
                                        cb.concat(
                                                cb.concat(user.get("lastName"), " "),
                                                user.get("firstName")
                                        )
                                ),
                                pattern
                        )
                );

            } catch (Exception ignored) {}


            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

}
