package com.example.transport.repository;

import com.example.transport.dto.TripSummaryDTO;
import com.example.transport.model.Staff;
import com.example.transport.model.Trip;
import com.example.transport.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {

    @EntityGraph(attributePaths = {"vehicle", "vehicle.driver"})
    Page<Trip> findAllByDeletedFalse(Pageable pageable);

    @Query("""
    SELECT new com.example.transport.dto.TripSummaryDTO(
        t.tripId,
        t.departureLocation,
        t.destinationLocation,
        t.price,
        v.vehiclePlate,
        v.vehicleType
    )
    FROM Trip t
    LEFT JOIN t.vehicle v
    WHERE t.deleted = false
""")
    Page<TripSummaryDTO> findAllTripsOptimized(Pageable pageable);
    boolean existsByVehicleAndDepartureDateTimeAfter(Vehicle vehicle, LocalDateTime now);
    boolean existsByVehicle_DriverAndDepartureDateTimeBetween(
            Staff driver,
            LocalDateTime start,
            LocalDateTime end
    );
}
