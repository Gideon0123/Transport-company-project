package com.example.transport.repository.specification;

import com.example.transport.enums.TripStatus;
import com.example.transport.model.Trip;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripSearchSpecs {

    public static Specification<Trip> keywordSearch(String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank() || keyword.length() < 2) {
                return null;
            }

            List<Predicate> predicates = new ArrayList<>();

            String pattern = keyword.toLowerCase() + "%";

            predicates.add(cb.like(cb.lower(root.get("departureLocation")), pattern));
            predicates.add(cb.like(cb.lower(root.get("destinationLocation")), pattern));

            try {
                Join<Object, Object> vehicle = root.join("vehicle", JoinType.LEFT);

                predicates.add(cb.like(cb.lower(vehicle.get("vehiclePlate")), pattern));
                predicates.add(cb.like(cb.lower(vehicle.get("vehicleType").as(String.class)), pattern));
            } catch (Exception ignored) {}

            try {
                TripStatus status = TripStatus.valueOf(keyword.toUpperCase());
                predicates.add(cb.equal(root.get("status"), status));
            } catch (Exception ignored) {}

            try {
                LocalDate date = LocalDate.parse(keyword);

                predicates.add(
                        cb.between(
                                root.get("departureDateTime"),
                                date.atStartOfDay(),
                                date.plusDays(1).atStartOfDay()
                        )
                );
            } catch (Exception e) {
                System.out.println("Date parse failed: " + keyword);
            }

            try {
                LocalDate date = LocalDate.parse(keyword);

                predicates.add(
                        cb.between(
                                root.get("bookingDate"),
                                date.atStartOfDay(),
                                date.plusDays(1).atStartOfDay()
                        )
                );
            } catch (Exception e) {
                System.out.println("Date parse failed: " + keyword);
            }

            try {
                BigDecimal price = new BigDecimal(keyword);
                predicates.add(cb.equal(root.get("price"), price));
            } catch (Exception ignored) {}

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}