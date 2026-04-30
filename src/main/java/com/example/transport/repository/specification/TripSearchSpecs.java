package com.example.transport.repository.specification;

import com.example.transport.enums.TripStatus;
import com.example.transport.enums.VehicleType;
import com.example.transport.model.Trip;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripSearchSpecs {

    public static Specification<Trip> hasVehicleType(String vehicleType) {
        return (root, query, criteriaBuilder) -> {
            try {
                VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
                return criteriaBuilder.equal(root.get("vehicle").get("vehicleType"), type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }
    public static Specification<Trip> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            if (keyword.length() < 2) return null;

            String pattern = keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("departureLocation")), pattern),
                    cb.like(cb.lower(root.get("destinationLocation")), pattern)
            );
        };
    }

    public static Specification<Trip> hasDepartureLocation(String departureLocation) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("departureLocation"), departureLocation);
    }
    public static Specification<Trip> hasDestinationLocation(String destinationLocation) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("destinationLocation"), destinationLocation);
    }
    public static Specification<Trip> bookedOn(LocalDate bookingDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bookingDate").as(LocalDate.class), bookingDate);
    }
    public static Specification<Trip> hasDepartureDate(LocalDate date) {
        return (root, query, cb) -> {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            return cb.between(root.get("departureDateTime"), startOfDay, endOfDay);
        };
    }
    public static Specification<Trip> hasPrice(BigDecimal price) {
        return (root, query, cb) ->
                cb.equal(root.get("price"), price);
    }
    public static Specification<Trip> hasTripStatus(String status) {
        return (root, query, cb) -> {
            try {
                TripStatus tripStatus = TripStatus.valueOf(status.toUpperCase());
                return cb.equal(root.get("status"), tripStatus);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }
    public static Specification<Trip> hasVehiclePlate(String plate) {
        return (root, query, cb) ->
                cb.equal(root.get("vehicle").get("vehiclePlate"), plate);
    }
}
