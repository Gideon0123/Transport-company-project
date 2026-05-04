package com.example.transport.repository.specification;

import com.example.transport.enums.VehicleStatus;
import com.example.transport.enums.VehicleType;
import com.example.transport.model.Staff;
import com.example.transport.model.User;
import com.example.transport.model.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
public class VehicleSearchSpecs {

    public static Specification<Vehicle> keywordSearch(String keyword) {
        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) return null;
            if (keyword.length() < 2) return null;

            String pattern = keyword.toLowerCase() + "%";

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.like(cb.lower(root.get("vehiclePlate")), pattern));

            try {
                VehicleType vehicleType = VehicleType.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("vehicleType"), vehicleType));
            } catch (IllegalArgumentException ignored) {}

            try {
                VehicleStatus status = VehicleStatus.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException ignored) {}

            try {
                Long driverId = Long.valueOf(keyword);
                predicates.add(cb.equal(root.get("driver").get("id"), driverId));
            } catch (NumberFormatException ignored) {}

            try {
                Join<Vehicle, Staff> staff = root.join("driver", JoinType.LEFT);
                Join<Staff, User> user = staff.join("user", JoinType.LEFT);

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
