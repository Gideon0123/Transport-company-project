package com.example.transport.repository.specification;

import com.example.transport.enums.BookingStatus;
import com.example.transport.model.CustomerTrip;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BookingSearchSpecs {

    public static Specification<CustomerTrip> keywordSearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            if (keyword.length() < 2) return null;

            System.out.println("Keyword = " + keyword);

            String pattern = keyword.toLowerCase() + "%";

            List<Predicate> predicates = new ArrayList<>();

            Join<Object, Object> user = root.join("customer", JoinType.LEFT);
            Join<Object, Object> trip = root.join("trip", JoinType.LEFT);

            try {
                BookingStatus status = BookingStatus.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (IllegalArgumentException ignored) {
            }

            predicates.add(cb.like(cb.lower(user.get("email")), pattern));
            predicates.add(cb.like(cb.lower(trip.get("departureLocation")), pattern));
            predicates.add(cb.like(cb.lower(trip.get("destinationLocation")), pattern));

            try {
                BigDecimal pricePerSeat = new BigDecimal(keyword);
                predicates.add(cb.equal(trip.get("price"), pricePerSeat));
            } catch (NumberFormatException ignored) {}

            try {
                LocalDate date = LocalDate.parse(keyword);

                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay();

                predicates.add(
                        cb.between(
                                trip.get("departureDateTime"),
                                start,
                                end
                        )
                );
            } catch (DateTimeParseException ignored) {}

            try {
                Long tripId = Long.valueOf(keyword);
                predicates.add(cb.equal(trip.get("id"), tripId));
            } catch (NumberFormatException ignored) {
            }

            try {
                BigDecimal totalPrice = new BigDecimal(keyword);
                predicates.add(cb.equal(root.get("totalPrice"), totalPrice));
            } catch (NumberFormatException ignored) {}

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CustomerTrip> hasTotalPrice(BigDecimal totalPrice) {
        return (root, query, cb) ->
                cb.equal(root.get("totalPrice"), totalPrice);
    }

    public static Specification<CustomerTrip> hasStatus(String status) {

        return (root, query, cb) -> {

            if (status == null || status.isBlank()) return null;
            try {
                BookingStatus bookingStatus = BookingStatus.valueOf(status.trim().toUpperCase());
                return cb.equal(root.get("status"), bookingStatus);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<CustomerTrip> hasTripId(Long tripId) {
        return (root, query, cb) ->
                cb.equal(root.get("trip").get("id"), tripId);
    }
}
