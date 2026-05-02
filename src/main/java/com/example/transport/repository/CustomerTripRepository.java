package com.example.transport.repository;

import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.model.CustomerTrip;
import com.example.transport.service.BookingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CustomerTripRepository extends JpaRepository<CustomerTrip, Long>, JpaSpecificationExecutor<CustomerTrip> {

    @Query("SELECT COALESCE(SUM(ct.numberOfSeats), 0) FROM CustomerTrip ct WHERE ct.trip.id = :tripId")
    int sumSeatsByTrip(Long tripId);

    Page<CustomerTrip> findByCustomerEmail(String email, Pageable pageable);

    @Query("""
    SELECT ct FROM CustomerTrip ct
    WHERE LOWER(ct.trip.departureLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(ct.trip.destinationLocation) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<CustomerTrip> searchBookings(String keyword, Pageable pageable);

    @Query("""
SELECT\s
    ct.bookingId AS bookingId,
    c.email AS email,
    t.id AS tripId,
    t.departureLocation AS departureLocation,
    t.destinationLocation AS destinationLocation,
    t.departureDateTime AS departureDateTime,
    ct.numberOfSeats AS numberOfSeats,
    t.price AS price,
    ct.totalPrice AS totalPrice,
    ct.status AS status
FROM CustomerTrip ct
JOIN ct.trip t
JOIN ct.customer c
WHERE ct.deleted = false
""")
    Page<BookingProjection> getAllBookingsOptimized(Pageable pageable);

    @Query("""
    SELECT b FROM CustomerTrip b
    JOIN FETCH b.customer
    JOIN FETCH b.trip
""")
    Page<CustomerTrip> findAllWithRelations(Pageable pageable);
}
