package com.example.transport.repository.specification;

import com.example.transport.model.Trip;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TripSearchSpecs {

    public static Specification<Trip> hasVehicleType(String vehicleType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("vehicleType"), vehicleType);
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
    public static Specification<Trip> hasDepartureDateTime(LocalDate departureDateTime) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("departureDateTime").as(LocalDate.class), departureDateTime);
    }
}
