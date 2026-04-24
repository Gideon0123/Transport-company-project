package com.example.transport.repository.specification;

import com.example.transport.enums.VehicleStatus;
import com.example.transport.enums.VehicleType;
import com.example.transport.model.Vehicle;
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

            // String fields → use LIKE
            predicates.add(cb.like(cb.lower(root.get("vehiclePlate")), pattern));

            // ENUM fields → use EQUAL
            try {
                VehicleType vehicleType = VehicleType.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("vehicleType"), vehicleType));
            } catch (IllegalArgumentException ignored) {}

            try {
                VehicleStatus status = VehicleStatus.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException ignored) {}

            // Relationship (driverId) → use EQUAL
            try {
                Long driverId = Long.valueOf(keyword);
                predicates.add(cb.equal(root.get("driver").get("id"), driverId));
            } catch (NumberFormatException ignored) {}

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Vehicle> hasDriverId(Long driverId) {
        return (root, query, cb) ->
                cb.equal(root.get("driver").get("id"), driverId);
    }

    public static Specification<Vehicle> hasVehiclePlate(String vehiclePlate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("vehiclePlate"), vehiclePlate);
    }

    public static Specification<Vehicle> hasVehicleType(String vehicleType) {
        return (root, query, cb) -> {
            try {
                VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
                return cb.equal(root.get("vehicleType"), type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Vehicle> hasVehicleStatus(String vehicleStatus) {
        return (root, query, cb) -> {
            if (vehicleStatus == null || vehicleStatus.isBlank()) return null;

            try {
                VehicleStatus vehicle = VehicleStatus.valueOf(vehicleStatus.trim().toUpperCase());
                return cb.equal(root.get("status"), vehicle);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + vehicleStatus);
            }
        };
    }

}
